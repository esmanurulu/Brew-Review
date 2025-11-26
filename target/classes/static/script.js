document.addEventListener("DOMContentLoaded", function() {
    
    
    const animationDuration = 5300; 

    setTimeout(function() {
        const intro = document.getElementById('intro-container');
        const login = document.getElementById('login-container');

     
        intro.style.display = 'none'; 
        
      
        login.style.display = 'block';
        setTimeout(() => {
            login.style.opacity = '1';
        }, 50);

    }, animationDuration);

    const form = document.getElementById('loginForm');
    if(form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(form);
            fetch('/login', { method: 'POST', body: formData })
            .then(response => response.json())
            .then(data => alert(data.message));
        });
    }
});