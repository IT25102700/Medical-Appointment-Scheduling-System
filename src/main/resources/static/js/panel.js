// Admin/Doctor panel shared nav
const PANEL_SIDEBAR_ADMIN = `
<div class="sidebar">
  <div style="padding:16px 20px;border-bottom:1px solid var(--border);">
    <a href="../../index.html" class="logo">
      <div class="logo-icon"><svg viewBox="0 0 24 24" style="width:18px;height:18px;fill:white"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 14H9V8h2v8zm4 0h-2V8h2v8z"/></svg></div>
      <span class="logo-text" style="font-size:1rem;">Prescripto</span>
    </a>
    <span style="font-size:0.7rem;background:var(--primary-light);color:var(--primary);padding:2px 8px;border-radius:50px;font-weight:600;margin-top:4px;display:inline-block;">Admin</span>
  </div>
  <ul class="sidebar-nav" style="margin-top:8px;">
    <li><a href="dashboard.html" class="${location.pathname.includes('dashboard')?'active':''}">
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/></svg>Dashboard</a></li>
    <li><a href="appointments.html" class="${location.pathname.includes('appointments')?'active':''}">
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M17 12h-5v5h5v-5zM16 1v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2h-1V1h-2zm3 18H5V8h14v11z"/></svg>Appointments</a></li>
    <li><a href="add-doctor.html" class="${location.pathname.includes('add-doctor')?'active':''}">
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>Add Doctor</a></li>
    <li><a href="doctors-list.html" class="${location.pathname.includes('doctors-list')?'active':''}">
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/></svg>Doctors List</a></li>
  </ul>
  <div style="margin-top:auto;padding:16px 20px;">
    <a href="../../pages/login.html" class="btn btn-danger btn-sm" style="width:100%;justify-content:center;">Logout</a>
  </div>
</div>`;

const PANEL_SIDEBAR_DOCTOR = `
<div class="sidebar">
  <div style="padding:16px 20px;border-bottom:1px solid var(--border);">
    <a href="../../index.html" class="logo">
      <div class="logo-icon"><svg viewBox="0 0 24 24" style="width:18px;height:18px;fill:white"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 14H9V8h2v8zm4 0h-2V8h2v8z"/></svg></div>
      <span class="logo-text" style="font-size:1rem;">Prescripto</span>
    </a>
    <span style="font-size:0.7rem;background:#f0fdf4;color:var(--success);padding:2px 8px;border-radius:50px;font-weight:600;margin-top:4px;display:inline-block;">Doctor</span>
  </div>
  <ul class="sidebar-nav" style="margin-top:8px;">
    <li><a href="dashboard.html" class="${location.pathname.includes('dashboard')?'active':''}">
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/></svg>Dashboard</a></li>
    <li><a href="appointments.html" class="${location.pathname.includes('appointments')?'active':''}">
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M17 12h-5v5h5v-5zM16 1v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2h-1V1h-2zm3 18H5V8h14v11z"/></svg>Appointments</a></li>
    <li><a href="profile.html" class="${location.pathname.includes('profile')?'active':''}">
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>Profile</a></li>
  </ul>
  <div style="margin-top:auto;padding:16px 20px;">
    <a href="../../pages/login.html" class="btn btn-danger btn-sm" style="width:100%;justify-content:center;">Logout</a>
  </div>
</div>`;

function injectAdminSidebar() {
  const el = document.getElementById('admin-sidebar');
  if (el) el.outerHTML = PANEL_SIDEBAR_ADMIN;
}
function injectDoctorSidebar() {
  const el = document.getElementById('doctor-sidebar');
  if (el) el.outerHTML = PANEL_SIDEBAR_DOCTOR;
}

function showToast(msg, type='success') {
  const t = document.createElement('div');
  t.className=`toast ${type}`;
  t.innerHTML=`<span class="toast-icon">✓</span> ${msg}`;
  document.body.appendChild(t);
  setTimeout(()=>t.classList.add('show'),100);
  setTimeout(()=>{t.classList.remove('show');setTimeout(()=>t.remove(),400);},3000);
}

document.addEventListener('DOMContentLoaded', () => {
  injectAdminSidebar();
  injectDoctorSidebar();
});
