import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import AOS from 'aos';
import 'aos/dist/aos.css';
import './index.css';
import App from './App.jsx';
import DarkModeProvider from './components/DarkModeProvider';
import { AuthProvider } from './context/AuthContext';

AOS.init({
  offset: 200,
  duration: 800,
  easing: 'ease-in-sine',
  delay: 100,
  once: true,
});

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter basename="/portal">
      <AuthProvider>
        <DarkModeProvider>
          <App />
        </DarkModeProvider>
      </AuthProvider>
    </BrowserRouter>
  </StrictMode>,
);
