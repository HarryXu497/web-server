const query = window.matchMedia("(max-width: 768px)");
const navLinks = document.querySelector(".nav__links");
const navHamburger = document.querySelector(".nav__links");

const handler = function (query) {
    console.log(query);
    console.log(query.matches);
    if (query.matches) {
        navLinks.classList.remove("hidden");
        navHamburger.classList.add("hidden");
    } else {
        navLinks.classList.add("hidden");
        navHamburger.classList.remove("hidden");
    }
}

handler(query);

query.addListener(handler);