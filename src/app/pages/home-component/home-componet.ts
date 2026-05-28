import { Component, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home-component.html',
  styleUrls: ['./home-component.css']
})

export class HomeComponent implements AfterViewInit {

  ngAfterViewInit(): void {

    // Hero Carousel
    const slides = document.querySelectorAll('.slide');
    const dotsContainer = document.getElementById('carouselDots');

    let current = 0;
    let timer: any;

    slides.forEach((_, i) => {
      const d = document.createElement('button');

      d.className = 'dot' + (i === 0 ? ' active' : '');

      d.addEventListener('click', () => goTo(i));

      dotsContainer?.appendChild(d);
    });

    const goTo = (n: number) => {

      slides[current].classList.remove('active');

      document.querySelectorAll('.dot')[current]
        .classList.remove('active');

      current = (n + slides.length) % slides.length;

      slides[current].classList.add('active');

      document.querySelectorAll('.dot')[current]
        .classList.add('active');

      resetTimer();
    };

    const resetTimer = () => {

      clearInterval(timer);

      timer = setInterval(() => {
        goTo(current + 1);
      }, 5000);
    };

    document.getElementById('nextSlide')
      ?.addEventListener('click', () => goTo(current + 1));

    document.getElementById('prevSlide')
      ?.addEventListener('click', () => goTo(current - 1));

    resetTimer();

    // THEMES CAROUSEL

    const track = document.getElementById('themesTrack');

    let themeOffset = 0;

    const cardW = 280;

    document.getElementById('themesNext')
      ?.addEventListener('click', () => {

        if (!track) return;

        const max =
          track.scrollWidth - track.parentElement!.offsetWidth;

        themeOffset = Math.min(themeOffset + cardW * 2, max);

        track.style.transform =
          `translateX(-${themeOffset}px)`;
      });

    document.getElementById('themesPrev')
      ?.addEventListener('click', () => {

        if (!track) return;

        themeOffset = Math.max(themeOffset - cardW * 2, 0);

        track.style.transform =
          `translateX(-${themeOffset}px)`;
      });

    // Scroll nav effect

    window.addEventListener('scroll', () => {

      document.querySelector('.nav')
        ?.classList.toggle('scrolled', window.scrollY > 60);

    });

    // Intersection Observer

    const observer = new IntersectionObserver((entries) => {

      entries.forEach(e => {

        if (e.isIntersecting) {
          e.target.classList.add('visible');
        }

      });

    }, { threshold: 0.1 });

    document.querySelectorAll(
      '.step-card, .event-card, .testimonial, .stat'
    ).forEach(el => observer.observe(el));

  }

}