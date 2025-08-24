// API base URL
const apiBaseUrl = 'http://localhost:8080';

// Global state for the current user
let user = { id: null, role: null };

// DOM elements
const loginFormContainer = document.getElementById('login-form-container');
const registrationFormContainer = document.getElementById('registration-form-container');
const messageElement = document.getElementById('message');

const loginForm = document.getElementById('login-form');
const customerRegForm = document.getElementById('customer-reg-form');
const ownerRegForm = document.getElementById('owner-reg-form');

// --- Helper Functions ---
function showMessage(msg, type = 'success') {
    messageElement.textContent = msg;
    messageElement.className = type;
}

function navigateTo(page) {
    window.location.href = page;
}

// --- API Functions ---
async function apiFetch(method, path, body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: body ? JSON.stringify(body) : null
    };
    const response = await fetch(`${apiBaseUrl}${path}`, options);
    const data = await response.text();
    if (!response.ok) {
        throw new Error(data || `API call failed with status ${response.status}`);
    }
    try {
        return JSON.parse(data);
    } catch (e) {
        // If the response is not valid JSON (e.g., a simple text message), return it as is.
        return data;
    }
}

// --- Authentication Logic ---
async function handleLogin(credentials) {
    try {
        const loggedInUser = await apiFetch('POST', '/user/login', credentials);
        user.id = loggedInUser.userId;
        user.role = loggedInUser.role;
        showMessage(`Login successful! Welcome, ${loggedInUser.fullName}.`, 'success');

        // Correctly save user data to sessionStorage before redirecting
        sessionStorage.setItem('userId', user.id);
        sessionStorage.setItem('role', user.role);

        switch (user.role) {
            case 'ADMIN':
                navigateTo('admin-dashboard.html');
                break;
            case 'CUSTOMER':
                navigateTo('customer-dashboard.html');
                break;
            case 'OWNER':
                navigateTo('owner-dashboard.html');
                break;
            default:
                showMessage('Invalid user role.', 'error');
        }
    } catch (error) {
        showMessage(`Login failed: ${error.message}`, 'error');
    }
}

async function handleRegister(type, formData) {
    try {
        const path = `/user/register/${type}`; // Correctly builds the path
        const responseMessage = await apiFetch('POST', path, formData);
        showMessage(responseMessage, 'success');

        // Hide registration forms and show login form
        loginFormContainer.classList.remove('hidden');
        registrationFormContainer.classList.add('hidden');
        customerRegForm.classList.add('hidden');
        ownerRegForm.classList.add('hidden');

    } catch (error) {
        showMessage(`Registration failed: ${error.message}`, 'error');
    }
}

// --- Event Listeners for the Main Page ---
if (loginForm) {
    loginForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        handleLogin({ email, password });
    });
}

const showRegistrationLink = document.getElementById('show-registration');
if (showRegistrationLink) {
    showRegistrationLink.addEventListener('click', (event) => {
        event.preventDefault();
        loginFormContainer.classList.add('hidden');
        registrationFormContainer.classList.remove('hidden');
    });
}

const backToLoginLink = document.getElementById('back-to-login');
if (backToLoginLink) {
    backToLoginLink.addEventListener('click', (event) => {
        event.preventDefault();
        loginFormContainer.classList.remove('hidden');
        registrationFormContainer.classList.add('hidden');
    });
}

const showCustomerRegBtn = document.getElementById('show-customer-reg');
if (showCustomerRegBtn) {
    showCustomerRegBtn.addEventListener('click', () => {
        customerRegForm.classList.remove('hidden');
        ownerRegForm.classList.add('hidden');
    });
}

const showOwnerRegBtn = document.getElementById('show-owner-reg');
if (showOwnerRegBtn) {
    showOwnerRegBtn.addEventListener('click', () => {
        ownerRegForm.classList.remove('hidden');
        customerRegForm.classList.add('hidden');
    });
}

if (customerRegForm) {
    customerRegForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const formData = Object.fromEntries(new FormData(event.target).entries());
        handleRegister('customer', formData);
    });
}

if (ownerRegForm) {
    ownerRegForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const formData = Object.fromEntries(new FormData(event.target).entries());
        handleRegister('owner', formData);
    });
}

// --- Dashboard Logic (Loads based on URL) ---
function initDashboard() {
    // Retrieve user data from sessionStorage
    user.id = sessionStorage.getItem('userId');
    user.role = sessionStorage.getItem('role');

    // If no user data, redirect to login
    if (!user.id || !user.role) {
        window.location.href = 'index.html';
        return;
    }

    // Add logout functionality
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            sessionStorage.clear();
            window.location.href = 'index.html';
        });
    }

    // Handle dynamic content based on role
    const dashboardContentDiv = document.getElementById('dashboard-content');

    if (user.role === 'ADMIN') {
        document.querySelectorAll('[data-action]').forEach(btn => {
            btn.addEventListener('click', async (event) => {
                const action = event.target.dataset.action;
                dashboardContentDiv.innerHTML = '<p>Loading...</p>';
                try {
                    const data = await apiFetch('GET', `/admin/${action.toLowerCase()}`);
                    dashboardContentDiv.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
                } catch (error) {
                    dashboardContentDiv.innerHTML = `<p class="error">Error: ${error.message}</p>`;
                }
            });
        });
    } else if (user.role === 'CUSTOMER') {
        document.querySelectorAll('[data-action]').forEach(btn => {
            btn.addEventListener('click', async (event) => {
                const action = event.target.dataset.action;
                dashboardContentDiv.innerHTML = '<p>Loading...</p>';
                try {
                    let data;
                    if (action === 'centers') {
                        data = await apiFetch('GET', '/customer/centers');
                    } else if (action === 'bookings') {
                        data = await apiFetch('GET', `/customer/bookings/${user.id}`);
                    } else if (action === 'balance') {
                        data = await apiFetch('GET', `/customer/balance/${user.id}`);
                    } else {
                        data = "Action not yet implemented.";
                    }
                    dashboardContentDiv.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
                } catch (error) {
                    dashboardContentDiv.innerHTML = `<p class="error">Error: ${error.message}</p>`;
                }
            });
        });
    } else if (user.role === 'OWNER') {
        document.querySelectorAll('[data-action]').forEach(btn => {
            btn.addEventListener('click', async (event) => {
                const action = event.target.dataset.action;
                const contentDiv = document.getElementById('dashboard-content');
                contentDiv.innerHTML = '<p>Loading...</p>';
                try {
                    let data;
                    if (action === 'centres') {
                        data = await apiFetch('GET', `/gymowner/centres/${user.id}`);
                    } else if (action === 'bookings') {
                        // This requires an additional input for gymId, so it's more complex.
                        data = "Action not yet implemented.";
                    } else {
                        data = "Action not yet implemented.";
                    }
                    contentDiv.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
                } catch (error) {
                    contentDiv.innerHTML = `<p class="error">Error: ${error.message}</p>`;
                }
            });
        });
    }
}

// Check if we are on a dashboard page and initialize it
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('-dashboard.html')) {
        initDashboard();
    }
});
