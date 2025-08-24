// API base URL
const apiBaseUrl = 'http://localhost:8080';

// Global state for the current user
const user = {
    id: parseInt(sessionStorage.getItem('userId')),
    role: sessionStorage.getItem('role')
};

// DOM elements
const dashboardContentDiv = document.getElementById('dashboard-content');
const logoutBtn = document.getElementById('logout-btn');

// --- Helper Functions ---
function showMessage(msg, type = 'success') {
    const messageDiv = document.createElement('div');
    messageDiv.className = type === 'success' ? 'success' : 'error';
    messageDiv.textContent = msg;
    dashboardContentDiv.prepend(messageDiv);
}

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
 * @returns {string} The HTML string for the table.
 */
function createTable(data) {
    if (!Array.isArray(data) || data.length === 0) {
        return '<p>No data found.</p>';
    }

    const headers = Object.keys(data[0]);
    let tableHtml = '<table class="min-w-full bg-white rounded-lg overflow-hidden shadow-lg">';

    tableHtml += '<thead><tr>';
    headers.forEach(header => {
        // Corrected regex for header formatting
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

function showAddWalletForm() {
    dashboardContentDiv.innerHTML = `
        <div class="card">
            <h3>Add Money to Wallet</h3>
            <form id="add-wallet-form">
                <div class="form-group">
                    <label for="wallet-amount">Amount to add:</label>
                    <input type="number" id="wallet-amount" name="amount" required>
                </div>
                <button type="submit" class="btn btn-primary">Add Funds</button>
            </form>
        </div>
    `;

    document.getElementById('add-wallet-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const amount = document.getElementById('wallet-amount').value;
        if (!amount || isNaN(amount)) {
            showMessage("Invalid amount. Please enter a number.", 'error');
            return;
        }

        try {
            const data = await apiFetch('POST', '/customer/wallet/add', { customerId: user.id, amount: parseInt(amount) });
            showMessage(data, 'success');
        } catch (error) {
            showMessage(`Error: ${error.message}`, 'error');
        }
    });
}

function showBookSlotForm() {
    dashboardContentDiv.innerHTML = `
        <div class="card">
            <h3>Book a Slot</h3>
            <form id="book-slot-form">
                <div class="form-group">
                    <label for="gym-id">Gym ID:</label>
                    <input type="number" id="gym-id" name="gymId" required>
                </div>
                <div class="form-group">
                    <label for="slot-id">Slot ID:</label>
                    <input type="number" id="slot-id" name="slotId" required>
                </div>
                <div class="form-group">
                    <label for="booking-date">Booking Date (YYYY-MM-DD):</label>
                    <input type="text" id="booking-date" name="date" required>
                </div>
                <button type="submit" class="btn btn-primary">Book</button>
            </form>
        </div>
    `;

    document.getElementById('book-slot-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const gymId = document.getElementById('gym-id').value;
        const slotId = document.getElementById('slot-id').value;
        const date = document.getElementById('booking-date').value;

        if (!gymId || !slotId || !date) {
            showMessage("All fields are required.", 'error');
            return;
        }

        try {
            const data = await apiFetch('POST', '/customer/slots/book', { customerId: user.id, gymId: parseInt(gymId), slotId: parseInt(slotId), date });
            showMessage(data, 'success');
        } catch (error) {
            showMessage(`Error: ${error.message}`, 'error');
        }
    });
}

function showEditDetailsForm() {
    dashboardContentDiv.innerHTML = `
        <div class="card">
            <h3>Edit My Details</h3>
            <form id="edit-details-form">
                <div class="form-group">
                    <label for="edit-fullname">Full Name:</label>
                    <input type="text" id="edit-fullname" name="fullName">
                </div>
                <div class="form-group">
                    <label for="edit-email">Email:</label>
                    <input type="email" id="edit-email" name="email">
                </div>
                <div class="form-group">
                    <label for="edit-password">Password:</label>
                    <input type="password" id="edit-password" name="password">
                </div>
                <div class="form-group">
                    <label for="edit-phone">Phone Number:</label>
                    <input type="text" id="edit-phone" name="userPhone">
                </div>
                <div class="form-group">
                    <label for="edit-city">City:</label>
                    <input type="text" id="edit-city" name="city">
                </div>
                <div class="form-group">
                    <label for="edit-pincode">Pin Code:</label>
                    <input type="number" id="edit-pincode" name="pinCode">
                </div>
                <button type="submit" class="btn btn-primary">Update</button>
            </form>
        </div>
    `;

    document.getElementById('edit-details-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = Object.fromEntries(new FormData(e.target).entries());

        const payload = { user: {}, customer: {} };
        Object.keys(formData).forEach(key => {
            const value = formData[key];
            if (value) {
                if (key === 'userPhone' || key === 'pinCode') {
                    payload.user[key] = parseInt(value);
                } else {
                    payload.user[key] = value;
                }
            }
        });

        try {
            const data = await apiFetch('PUT', `/customer/details/${user.id}`, payload);
            showMessage(data, 'success');
        } catch (error) {
            showMessage(`Error: ${error.message}`, 'error');
        }
    });
}

function showEditPaymentDetailsForm() {
    dashboardContentDiv.innerHTML = `
        <div class="card">
            <h3>Edit Payment Details</h3>
            <form id="edit-payment-form">
                <div class="form-group">
                    <label for="payment-type">Payment Type (1 for Card, 2 for UPI):</label>
                    <input type="number" id="payment-type" name="paymentType" required>
                </div>
                <div class="form-group">
                    <label for="payment-info">Payment Info:</label>
                    <input type="text" id="payment-info" name="paymentInfo" required>
                </div>
                <button type="submit" class="btn btn-primary">Update</button>
            </form>
        </div>
    `;

    document.getElementById('edit-payment-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const paymentType = document.getElementById('payment-type').value;
        const paymentInfo = document.getElementById('payment-info').value;

        const payload = { paymentType: parseInt(paymentType), paymentInfo };
        try {
            const data = await apiFetch('PUT', `/customer/payments/edit/${user.id}`, payload);
            showMessage(data, 'success');
        } catch (error) {
            showMessage(`Error: ${error.message}`, 'error');
        }
    });
}


// --- Customer Dashboard Logic ---
function handleCustomerActions() {
    document.querySelectorAll('[data-action]').forEach(btn => {
        btn.addEventListener('click', async (event) => {
            const action = event.target.dataset.action;
            const contentDiv = document.getElementById('dashboard-content');
            contentDiv.innerHTML = '<p>Loading...</p>';
            try {
                let data;
                if (action === 'viewCenters') {
                    const centers = await apiFetch('GET', '/customer/centers');
                    contentDiv.innerHTML = `<h3>Available Gym Centers</h3><div class="table-container">${createTable(centers)}</div>`;
                } else if (action === 'viewBookedSlots') {
                    const bookings = await apiFetch('GET', `/customer/bookings/${user.id}`);
                    contentDiv.innerHTML = `<h3>My Bookings</h3><div class="table-container">${createTable(bookings)}</div>`;
                } else if (action === 'balance') {
                    const balance = await apiFetch('GET', `/customer/balance/${user.id}`);
                    contentDiv.innerHTML = `<h3>Current Wallet Balance</h3><pre>$${balance}</pre>`;
                } else if (action === 'addWallet') {
                    showAddWalletForm();
                } else if (action === 'bookSlot') {
                    showBookSlotForm();
                } else if (action === 'editDetails') {
                    showEditDetailsForm();
                } else if (action === 'editPaymentDetails') {
                    showEditPaymentDetailsForm();
                }

            } catch (error) {
                contentDiv.innerHTML = `<p class="error">Error: ${error.message}</p>`;
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
    if (user.role === 'CUSTOMER') {
        handleCustomerActions();
    } else {
        window.location.href = 'index.html';
    }
});
