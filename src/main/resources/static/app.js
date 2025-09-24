document.addEventListener('DOMContentLoaded', () => {
    // =================================================================================
    // CONFIGURAÇÃO
    // =================================================================================
    const API_BASE_URL = 'http://localhost:8080';

    const lineToBusMap = {
        // !!! SUBSTITUA ESTE UUID PELO ID DE UMA LINHA REAL DO SEU BANCO DE DADOS !!!
        "72499d24-73d5-42c7-af4c-7c67268a585e": "B-101",
    };

    // =================================================================================
    // ESTADO DA APLICAÇÃO
    // =================================================================================
    const state = {
        jwtToken: localStorage.getItem('jwtToken'),
        userEmail: localStorage.getItem('userEmail'),
        notificationSocket: null,
        trackingSocket: null,
        map: null,
        busMarker: null,
    };

    // =================================================================================
    // ELEMENTOS DA DOM
    // =================================================================================
    const views = {
        login: document.getElementById('login-view'),
        main: document.getElementById('main-view'),
    };
    const contentDiv = document.getElementById('content');
    const modal = document.getElementById('pix-modal');
    const loginForm = document.getElementById('login-form');
    const logoutButton = document.getElementById('logout-button');
    const welcomeMessage = document.getElementById('welcome-message');
    const navLinks = {
        lines: document.getElementById('nav-lines'),
        myTickets: document.getElementById('nav-my-tickets')
    };
    const modalCloseButton = document.querySelector('.close-modal');
    const qrCodeContainer = document.getElementById('qr-code-container');
    const paymentStatusEl = document.getElementById('payment-status');
    const backToTicketsButton = document.getElementById('back-to-tickets-button');
    let busTrack = [];
    let trackPolyline = null;
    // =================================================================================
    // MÓDULO DE API
    // =================================================================================
    const api = {
        async call(endpoint, method = 'GET', body = null) {
            const headers = { 'Content-Type': 'application/json' };
            if (state.jwtToken) {
                headers['Authorization'] = `Bearer ${state.jwtToken}`;
            }
            const options = { method, headers, body: body ? JSON.stringify(body) : null };
            const response = await fetch(`${API_BASE_URL}/api/v1${endpoint}`, options);
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(`Falha na chamada da API: ${response.statusText} - ${errorBody}`);
            }
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                return response.json();
            }
        },
        login: (email, password) => api.call('/login', 'POST', { email, password }),
        getLines: () => api.call('/lines'),
        getMyTickets: () => api.call('/tickets'),
        purchaseTicket: (lineId) => api.call('/tickets', 'POST', { lineId })
    };

    // =================================================================================
    // NAVEGAÇÃO / ROTEAMENTO
    // =================================================================================
    function navigateTo(viewName) {
        Object.values(views).forEach(view => {
            if (view) view.classList.remove('active');
        });
        
        if (views[viewName]) {
            views[viewName].classList.add('active');
        }

        // Carrega o conteúdo apropriado para a view principal
        if (viewName === 'main') {
            welcomeMessage.textContent = `Bem-vindo, ${state.userEmail}!`;
            loadLinesView(); // Carrega a lista de linhas como tela inicial
        }
    }

    // =================================================================================
    // FUNÇÕES DE RENDERIZAÇÃO
    // =================================================================================
    function renderLines(lines = []) {
        contentDiv.innerHTML = '<h2>Selecione uma Linha para Comprar</h2><div class="card-list"></div>';
        const list = contentDiv.querySelector('.card-list');
        if (lines.length === 0) { list.innerHTML = "<p>Nenhuma linha disponível.</p>"; return; }
        lines.forEach(line => {
            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `<h3>${line.name}</h3>`;
            card.onclick = () => renderPurchaseView(line);
            list.appendChild(card);
        });
    }

    function renderPurchaseView(line) {
        contentDiv.innerHTML = `
            <h2>Comprar Ticket</h2>
            <p>Você está comprando um ticket para a linha:</p>
            <h3>${line.name}</h3>
            <p><strong>Preço:</strong> R$ 15.00</p> 
            <button id="confirm-purchase">Confirmar Compra e Gerar PIX</button>
            <br/><br/>
            <a href="#" id="back-to-lines">Voltar para a lista de linhas</a>
        `;
        document.getElementById('confirm-purchase').onclick = () => handlePurchase(line.id);
        document.getElementById('back-to-lines').onclick = (e) => { e.preventDefault(); loadLinesView(); };
    }

    function renderMyTickets(tickets = []) {
        contentDiv.innerHTML = '<h2>Meus Tickets (Clique em um ticket disponível para rastrear)</h2><div class="card-list"></div>';
        const list = contentDiv.querySelector('.card-list');
        if (tickets.length === 0) { list.innerHTML = "<p>Você ainda não comprou nenhum ticket.</p>"; return; }
        tickets.forEach(ticket => {
            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `
                <h3>Ticket #${ticket.Id.substring(0, 8)}...</h3>
                <p>Linha: ${ticket.lineName}</p>
                <p>Status: <span class="card-status status-${ticket.status.toLowerCase()}">${ticket.status}</span></p>`;
            const isAvailable = ticket.status === 'AVAILABLE' || ticket.status === 'PAID';
            card.style.cursor = isAvailable ? 'pointer' : 'not-allowed';
            if (isAvailable) {
                card.onclick = () => navigateToMapView(ticket.lineId, ticket.lineName);
            }
            list.appendChild(card);
        });
    }

    // =================================================================================
    // LÓGICA DE EVENTOS E FLUXOS
    // =================================================================================
    async function loadLinesView() {
        try {
            const pageOfLines = await api.getLines();
            renderLines(pageOfLines.content || []);
        } catch (error) {
            console.error('Erro ao carregar linhas:', error);
            contentDiv.innerHTML = "<p style='color:red;'>Não foi possível carregar as linhas.</p>";
        }
    }

    async function loadMyTicketsView() {
        try {
            const pageOfTickets = await api.getMyTickets();
            renderMyTickets(pageOfTickets.content || []);
        } catch (error) {
            console.error('Erro ao carregar tickets:', error);
            contentDiv.innerHTML = "<p style='color:red;'>Não foi possível carregar seus tickets.</p>";
        }
    }

    async function handlePurchase(lineId) {
        try {
            const data = await api.purchaseTicket(lineId);
            qrCodeContainer.innerHTML = `<img src="${data.qrCodeBase64}" alt="QR Code PIX">`;
            paymentStatusEl.textContent = 'Aguardando Pagamento...';
            paymentStatusEl.className = '';
            modal.style.display = 'flex';
        } catch (error) {
            console.error('Erro ao gerar PIX:', error);
            alert('Falha ao gerar o PIX. Tente novamente.');
        }
    }
    
    // =================================================================================
    // LÓGICA DO MAPA E TRACKING
    // =================================================================================
    function navigateToMapView(lineId, lineName) {
         Object.values(views).forEach(view => {
            if (view) view.classList.remove('active');
         });

        // mostra o mapa
        const mapView = document.getElementById('map-view');
        mapView.style.display = 'block';
        mapView.classList.add('active');

        document.getElementById('map-line-name').textContent = `Rastreando: ${lineName}`;

        // inicializa mapa se ainda não foi criado
        if (!state.map) {
            state.map = L.map('map-container').setView([-28.4607, -54.6565], 15);
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png')
                .addTo(state.map);
        }

        // força o Leaflet a redesenhar no tamanho correto
        setTimeout(() => {
            state.map.invalidateSize();
        }, 200);

        // conecta ao ônibus
        const busId = 'd3128c54-b72a-48f4-b86d-b30e8bf0a830';
        if (!busId) {
            alert("Desculpe, não há um ônibus ativo para rastrear nesta linha no momento.");
            loadMyTicketsView();
            mapView.style.display = 'none';
            return;
        }
        connectBusTrackingSocket(busId);
    }

    function initMap() {
        if (state.map) {
             state.map.invalidateSize();
             return;
        }
        state.map = L.map('map-container').setView([-28.4607, -54.6565], 15);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(state.map);
    }

    function connectBusTrackingSocket(busId) {
        if (state.trackingSocket) state.trackingSocket.close();
        if (!state.jwtToken) return;

        const statusEl = document.getElementById('tracking-status');
        statusEl.textContent = `Conectando ao ônibus ${busId}...`;
        
        const host = API_BASE_URL.replace(/^http/, 'ws');
        const wsUrl = `${host}/ws/bus/${busId}?token=${state.jwtToken}`;
        state.trackingSocket = new WebSocket(wsUrl);

        state.trackingSocket.onopen = () => statusEl.textContent = `Conectado! Aguardando localização...`;
        state.trackingSocket.onmessage = (event) => {
            statusEl.textContent = `Localização recebida!`;
            const pos = JSON.parse(event.data);
        if (pos.latitude && pos.longitude) {
                updateBusMarker([pos.latitude, pos.longitude]);
            }
        };
        state.trackingSocket.onclose = () => statusEl.textContent = `Desconectado.`;
    }

    function updateBusMarker(position) {
        console.log("Posição recebida:", position);
        if (position.lat !== undefined && position.lng !== undefined) {
            position = [position.lat, position.lng];
        }
        if (!Array.isArray(position)) {
        console.error("Formato inválido de posição:", position);
        return;
        }

        busTrack.push(position);
        console.log("Histórico:", state.busTrack);
        if (!state.busMarker) {
            const busIcon = L.icon({
                iconUrl: 'https://cdn-icons-png.flaticon.com/64/3448/3448339.png',
                iconSize: [48, 48], iconAnchor: [24, 24],
            });
            state.busMarker = L.marker(position, { icon: busIcon }).addTo(state.map);
        } else {
            state.busMarker.setLatLng(position);
        }
        state.map.panTo(position);

        // Desenha o rastro
        if (busTrack.length > 1) {
            if (!trackPolyline) {
                trackPolyline = L.polyline(busTrack, {
                    color: '#0033ff',
                    weight: 5,
                    opacity: 0.7
                }).addTo(state.map);
            } else {
                trackPolyline.addLatLng(position);
            }
        }
    }

    // =================================================================================
    // WEBSOCKET DE NOTIFICAÇÃO
    // =================================================================================
    function connectNotificationSocket() {
        if (state.notificationSocket) state.notificationSocket.close();
        
        const host = API_BASE_URL.replace(/^http/, 'ws');
        const wsUrl = `${host}/ws/bus/notifications?token=${state.jwtToken}`;
        state.notificationSocket = new WebSocket(wsUrl);

        state.notificationSocket.onopen = () => console.log('Socket de Notificação Conectado!');
        state.notificationSocket.onmessage = (event) => {
            const message = JSON.parse(event.data);
            if (message.type === 'PAYMENT_CONFIRMED') {
                if (modal.style.display === 'flex') {
                    paymentStatusEl.textContent = 'Pagamento APROVADO!';
                    paymentStatusEl.className = 'paid';
                }
                alert('Seu pagamento foi confirmado!');
                loadMyTicketsView();
            }
        };
    }

    // =================================================================================
    // INICIALIZAÇÃO DA APLICAÇÃO
    // =================================================================================
    function init() {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('login-email').value;
            const password = document.getElementById('login-password').value;
            try {
                const data = await api.login(email, password);
                state.jwtToken = data.token;
                state.userEmail = email;
                localStorage.setItem('jwtToken', data.token);
                localStorage.setItem('userEmail', email);
                navigateTo('main');
                connectNotificationSocket();
            } catch (error) {
                alert('Falha no login. Verifique suas credenciais.');
            }
        });

        logoutButton.addEventListener('click', () => {
            state.jwtToken = null;
            state.userEmail = null;
            localStorage.clear();
            if (state.notificationSocket) state.notificationSocket.close();
            if (state.trackingSocket) state.trackingSocket.close();
            navigateTo('login');
        });

        navLinks.lines.addEventListener('click', (e) => { e.preventDefault(); loadLinesView(); });
        navLinks.myTickets.addEventListener('click', (e) => { e.preventDefault(); loadMyTicketsView(); });
        modalCloseButton.addEventListener('click', () => { modal.style.display = 'none'; });
        backToTicketsButton.addEventListener('click', () => {
            if (state.trackingSocket) state.trackingSocket.close();
            if(state.busMarker) { state.map.removeLayer(state.busMarker); state.busMarker = null; }
            if (trackPolyline) {
                state.map.removeLayer(trackPolyline);
                trackPolyline = null;
            }
            busTrack = [];
            document.getElementById('map-view').style.display = 'none';
            navigateTo('main');
            loadMyTicketsView();
        });

        if (state.jwtToken) {
            navigateTo('main');
            connectNotificationSocket();
        } else {
            navigateTo('login');
        }
    }

    init();
});