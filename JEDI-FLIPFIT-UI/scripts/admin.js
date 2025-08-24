// API base URL
const apiBaseUrl = 'http://localhost:8080';

// Global state for the current user (retrieved from sessionStorage)
const user = {
    id: parseInt(sessionStorage.getItem('userId')),
    role: sessionStorage.getItem('role')
};

// DOM elements
const dashboardContentDiv = document.getElementById('dashboard-content');
const logoutBtn = document.getElementById('logout-btn');

// --- Helper Functions ---

// Function to display messages in the dashboard
function showMessage(msg, type = 'success') {
    const messageDiv = document.createElement('div');
    messageDiv.className = type === 'success' ? 'success' : 'error';
    messageDiv.textContent = msg;
    dashboardContentDiv.prepend(messageDiv);
}

// Function to make API calls
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
        return data;
    }
}

/**
 * Creates a dynamic HTML table from a JSON array.
 * @param {Array} data The array of objects to display.
 * @param {string} dataType The type of data (e.g., 'gymowners', 'customers').
 * @returns {string} The HTML string for the table.
 */
function createTable(data, dataType) {
    if (!Array.isArray(data) || data.length === 0) {
        return '<p>No data found.</p>';
    }

    let headers = Object.keys(data[0]);

    // Filter out specific headers based on data type
    if (dataType.includes('gymowners') || dataType.includes('customers')) {
        headers = headers.filter(header =>
            header !== 'password' &&
            header !== 'city' &&
            header !== 'pinCode' &&
            header !== 'role'
        );
    }

    let tableHtml = '<table class="min-w-full bg-white rounded-lg overflow-hidden shadow-lg">';

    tableHtml += '<thead><tr>';
    headers.forEach(header => {
        const formattedHeader = header.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase());
        tableHtml += `<th class="px-6 py-3 border-b-2 border-gray-300 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">${formattedHeader}</th>`;
    });
    tableHtml += '</tr></thead>';

    tableHtml += '<tbody>';
    data.forEach(item => {
        tableHtml += '<tr>';
        headers.forEach(header => {
            const value = item[header] !== undefined ? item[header] : 'N/A';
            tableHtml += `<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 border-b border-gray-200">${value}</td>`;
        });
        tableHtml += '</tr>';
    });
    tableHtml += '</tbody></table>';

    return tableHtml;
}

// --- Admin Dashboard Logic ---

// Handles all button clicks in the admin dashboard
function handleAdminActions() {
    document.querySelectorAll('[data-action]').forEach(btn => {
        btn.addEventListener('click', async (event) => {
            const action = event.target.dataset.action;
            const dashboardContentDiv = document.getElementById('dashboard-content');
            dashboardContentDiv.innerHTML = '<p>Loading...</p>';
            try {
                let data;
                let identifier;
                let apiUrl;

                // Corrected logic to build the API URL based on the action
                if (action === 'approve-owner') {
                    identifier = prompt('Enter owner email:');
                    if (!identifier) {
                        dashboardContentDiv.innerHTML = '<p class="error">Action cancelled.</p>';
                        return;
                    }
                    apiUrl = `/admin/gymowners/approve/${identifier}`;
                } else if (action === 'approve-gym') {
                    identifier = prompt('Enter gym ID:');
                    if (!identifier) {
                        dashboardContentDiv.innerHTML = '<p class="error">Action cancelled.</p>';
                        return;
                    }
                    apiUrl = `/admin/gyms/approve/${identifier}`;
                } else if (action === 'delete-user') {
                    identifier = prompt('Enter user ID:');
                    if (!identifier) {
                        dashboardContentDiv.innerHTML = '<p class="error">Action cancelled.</p>';
                        return;
                    }
                    apiUrl = `/admin/users/${identifier}`;
                } else if (action === 'delete-gym') {
                    identifier = prompt('Enter gym ID:');
                    if (!identifier) {
                        dashboardContentDiv.innerHTML = '<p class="error">Action cancelled.</p>';
                        return;
                    }
                    apiUrl = `/admin/gyms/${identifier}`;
                } else {
                    // For GET requests like viewing all gyms or customers
                    apiUrl = `/admin/${action}`;
                }

                if (action.includes('approve') || action.includes('delete')) {
                    data = await apiFetch(action.includes('approve') ? 'POST' : 'DELETE', apiUrl);
                } else {
                    data = await apiFetch('GET', apiUrl);
                }

                if (Array.isArray(data)) {
                    dashboardContentDiv.innerHTML = `<div class="table-container">${createTable(data, action)}</div>`;
                } else {
                    dashboardContentDiv.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
                }
            } catch (error) {
                dashboardContentDiv.innerHTML = `<p class="error">Error: ${error.message}</p>`;
            }
        });
    });
}

// Handles logout
if (logoutBtn) {
    logoutBtn.addEventListener('click', () => {
        sessionStorage.clear();
        window.location.href = 'index.html';
    });
}

// Initialize the dashboard on page load
document.addEventListener('DOMContentLoaded', () => {
    if (user.role === 'ADMIN') {
        handleAdminActions();
    } else {
        window.location.href = 'index.html';
    }
});
