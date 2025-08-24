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

// --- Dynamic Forms and Dashboard Actions ---

function showAddCentreForm() {
    dashboardContentDiv.innerHTML = `
        <div class="card">
            <h3>Add New Gym Centre</h3>
            <form id="add-centre-form">
                <div class="form-group">
                    <label for="centre-name">Gym Name:</label>
                    <input type="text" id="centre-name" name="centreName" required>
                </div>
                <div class="form-group">
                    <label for="capacity">Capacity:</label>
                    <input type="number" id="capacity" name="capacity" required>
                </div>
                <div class="form-group">
                    <label for="cost">Cost per Slot:</label>
                    <input type="number" id="cost" name="cost" required>
                </div>
                <div class="form-group">
                    <label for="city">City:</label>
                    <input type="text" id="city" name="city" required>
                </div>
                <div class="form-group">
                    <label for="state">State:</label>
                    <input type="text" id="state" name="state" required>
                </div>
                <div class="form-group">
                    <label for="pincode">Pin Code:</label>
                    <input type="text" id="pincode" name="pincode" required>
                </div>
                <div class="form-group">
                    <label for="facilities">Facilities (comma-separated):</label>
                    <input type="text" id="facilities" name="facilities">
                </div>
                <button type="submit" class="btn btn-primary">Add Centre</button>
            </form>
        </div>
    `;

    document.getElementById('add-centre-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = Object.fromEntries(new FormData(e.target).entries());

        const payload = {
            ownerId: user.id,
            centreName: formData.centreName,
            capacity: parseInt(formData.capacity),
            cost: parseInt(formData.cost),
            approved: false, // Always false on creation
            city: formData.city,
            state: formData.state,
            pincode: formData.pincode,
            facilities: formData.facilities
        };

        try {
            const response = await apiFetch('POST', '/gymowner/centres/add', payload);
            showMessage(response, 'success');
        } catch (error) {
            showMessage(`Error: ${error.message}`, 'error');
        }
    });
}

function showViewBookingsForm() {
    dashboardContentDiv.innerHTML = `
        <div class="card">
            <h3>View Bookings</h3>
            <form id="view-bookings-form">
                <div class="form-group">
                    <label for="gym-id">Gym ID:</label>
                    <input type="number" id="gym-id" name="gymId" required>
                </div>
                <button type="submit" class="btn btn-primary">View</button>
            </form>
        </div>
    `;

    document.getElementById('view-bookings-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const gymId = document.getElementById('gym-id').value;
        if (!gymId || isNaN(gymId)) {
            showMessage("Invalid gym ID.", 'error');
            return;
        }

        try {
            const bookings = await apiFetch('GET', `/gymowner/bookings/${user.id}/${gymId}`);
            dashboardContentDiv.innerHTML = `<h3>Bookings for Gym ${gymId}</h3>${createTable(bookings)}`;
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
                <div class="form-group">
                    <label for="edit-pan">PAN:</label>
                    <input type="text" id="edit-pan" name="pan">
                </div>
                <div class="form-group">
                    <label for="edit-aadhaar">Aadhaar:</label>
                    <input type="text" id="edit-aadhaar" name="aadhaar">
                </div>
                <div class="form-group">
                    <label for="edit-gst">GST:</label>
                    <input type="text" id="edit-gst" name="gst">
                </div>
                <button type="submit" class="btn btn-primary">Update</button>
            </form>
        </div>
    `;

    document.getElementById('edit-details-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = Object.fromEntries(new FormData(e.target).entries());

        const userPayload = {};
        const gymOwnerPayload = {};

        Object.keys(formData).forEach(key => {
            const value = formData[key];
            if (value) {
                if (['fullName', 'email', 'password', 'userPhone', 'city', 'pinCode'].includes(key)) {
                    if (key === 'userPhone' || key === 'pinCode') {
                        userPayload[key] = parseInt(value);
                    } else {
                        userPayload[key] = value;
                    }
                } else if (['pan', 'aadhaar', 'gst'].includes(key)) {
                    gymOwnerPayload[key] = value;
                }
            }
        });

        const payload = { user: userPayload, gymOwner: gymOwnerPayload };
        try {
            const data = await apiFetch('PUT', `/gymowner/details/${user.id}`, payload);
            showMessage(`Details updated successfully.`, 'success');
        } catch (error) {
            showMessage(`Error: ${error.message}`, 'error');
        }
    });
}


// --- Gym Owner Dashboard Logic ---
function handleOwnerActions() {
    document.querySelectorAll('[data-action]').forEach(btn => {
        btn.addEventListener('click', async (event) => {
            const action = event.target.dataset.action;
            const contentDiv = document.getElementById('dashboard-content');
            contentDiv.innerHTML = '<p>Loading...</p>';
            try {
                let data;
                if (action === 'addCentre') {
                    showAddCentreForm();
                } else if (action === 'viewGymDetails') {
                    const centres = await apiFetch('GET', `/gymowner/centres/${user.id}`);
                    contentDiv.innerHTML = `<h3>My Gym Centres</h3><div class="table-container">${createTable(centres)}</div>`;
                } else if (action === 'viewBookings') {
                    showViewBookingsForm();
                } else if (action === 'editDetails') {
                    showEditDetailsForm();
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
    if (user.role === 'OWNER') {
        handleOwnerActions();
    } else {
        window.location.href = 'index.html';
    }
});
