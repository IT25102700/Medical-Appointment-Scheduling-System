// Shared navigation injection
const NAV_HTML = `
<nav>
  <a href="../index.html" class="logo">
    <div class="logo-icon">
      <svg viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 14H9V8h2v8zm4 0h-2V8h2v8z"/></svg>
    </div>
    <span class="logo-text">Prescripto</span>
  </a>
  <ul class="nav-links">
    <li><a href="../index.html" id="nav-home">HOME</a></li>
    <li><a href="doctors.html" id="nav-doctors">ALL DOCTORS</a></li>
    <li><a href="about.html" id="nav-about">ABOUT</a></li>
    <li><a href="contact.html" id="nav-contact">CONTACT</a></li>
  </ul>
  <div class="nav-actions">
    <a href="login.html" class="btn btn-outline btn-sm" id="nav-login">Login</a>
    <a href="register.html" class="btn btn-primary btn-sm" id="nav-register">Create account</a>
  </div>
</nav>`;

const FOOTER_HTML = `
<footer>
  <div class="footer-grid">
    <div class="footer-brand">
      <a href="../index.html" class="logo" style="margin-bottom:0">
        <div class="logo-icon"><svg viewBox="0 0 24 24" style="width:20px;height:20px;fill:white"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 14H9V8h2v8zm4 0h-2V8h2v8z"/></svg></div>
        <span class="logo-text" style="color:white">Prescripto</span>
      </a>
      <p>Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s.</p>
    </div>
    <div class="footer-col">
      <h4>COMPANY</h4>
      <ul>
        <li><a href="../index.html">Home</a></li>
        <li><a href="about.html">About us</a></li>
        <li><a href="#">Delivery</a></li>
        <li><a href="#">Privacy policy</a></li>
      </ul>
    </div>
    <div class="footer-col footer-contact">
      <h4>GET IN TOUCH</h4>
      <p>+1-212-456-7890</p>
      <p>greatstackdev@gmail.com</p>
    </div>
  </div>
  <div class="footer-bottom">Copyright 2024 © Prescripto.com - All Right Reserved.</div>
</footer>`;

function injectNav() {
  const placeholder = document.getElementById('nav-placeholder');
  if (placeholder) placeholder.outerHTML = NAV_HTML;
  // highlight active
  const path = window.location.pathname;
  if (path.includes('doctors')) document.getElementById('nav-doctors')?.classList.add('active');
  else if (path.includes('about')) document.getElementById('nav-about')?.classList.add('active');
  else if (path.includes('contact')) document.getElementById('nav-contact')?.classList.add('active');
  else document.getElementById('nav-home')?.classList.add('active');
}

function injectFooter() {
  const placeholder = document.getElementById('footer-placeholder');
  if (placeholder) placeholder.outerHTML = FOOTER_HTML;
}

function showToast(msg, type = 'success') {
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span class="toast-icon">✓</span> ${msg}`;
  document.body.appendChild(toast);
  setTimeout(() => toast.classList.add('show'), 100);
  setTimeout(() => { toast.classList.remove('show'); setTimeout(() => toast.remove(), 400); }, 3000);
}

// Slots interaction
function initSlots() {
  document.querySelectorAll('.day-chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('.day-chip').forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
    });
  });
  document.querySelectorAll('.time-chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('.time-chip').forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
    });
  });
  const bookBtn = document.getElementById('book-btn');
  if (bookBtn) bookBtn.addEventListener('click', () => {
    const day = document.querySelector('.day-chip.active');
    const time = document.querySelector('.time-chip.active');
    if (!day || !time) { showToast('Please select a date and time', 'error'); return; }
    showToast('Appointment booked successfully!');
    setTimeout(() => window.location.href = 'my-appointments.html', 1500);
  });
}

// Specialty filter on doctors page
function initFilter() {
  document.querySelectorAll('.specialty-card').forEach(card => {
    card.addEventListener('click', () => {
      document.querySelectorAll('.specialty-card').forEach(c => c.classList.remove('active'));
      card.classList.add('active');
      const spec = card.dataset.specialty;
      document.querySelectorAll('.doctor-card').forEach(dc => {
        dc.style.display = (spec === 'all' || dc.dataset.specialty === spec) ? '' : 'none';
      });
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  injectNav();
  injectFooter();
  initSlots();
  initFilter();
});
