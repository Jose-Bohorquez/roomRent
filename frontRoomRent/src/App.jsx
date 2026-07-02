import { Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "./layouts/MainLayout";

import Home from "./sections/Home";
import About from "./sections/About";
import PopularAreas from "./sections/PopularAreas";
import Services from "./sections/Services";
import Clients from "./sections/Clients";
import Contact from "./sections/Contact";

import Login from "./components/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import PropertiesPage from "./pages/PropertiesPage";
import PropertyDetail from "./pages/PropertyDetail";
import ArrendadorDashboard from "./pages/ArrendadorDashboard";
import ArrendatarioDashboard from "./pages/ArrendatarioDashboard";

const App = () => {
  return (
    <Routes>

      {/* LANDING y navegacion publica */}
      <Route element={<MainLayout />}>
        <Route
          index
          element={
            <>
              <Home />
              <About />
              <PopularAreas />
              <Clients />
              <Services />
              <Contact />
            </>
          }
        />
        <Route path="properties" element={<PropertiesPage />} />
        <Route path="properties/:id" element={<PropertyDetail />} />
      </Route>

      {/* AUTH */}
      <Route path="/login" element={<Login />} />

      {/* DASHBOARD ARRENDADOR */}
      <Route
        path="/arrendador"
        element={
          <ProtectedRoute requiredRole="ROLE_USER">
            <ArrendadorDashboard />
          </ProtectedRoute>
        }
      />

      {/* DASHBOARD ARRENDATARIO */}
      <Route
        path="/arrendatario"
        element={
          <ProtectedRoute requiredRole="ROLE_USER">
            <ArrendatarioDashboard />
          </ProtectedRoute>
        }
      />

      {/* ADMIN redirige al panel de JHipster */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute requiredRole="ROLE_ADMIN">
            {/* El panel admin completo vive en JHipster :8080 */}
            <AdminRedirect />
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

function AdminRedirect() {
  // Redirect to Angular admin panel (entity CRUDs). JWT is already in
  // localStorage['jhi-authenticationToken'] so Angular picks it up automatically.
  window.location.href = "/inmueble";
  return null;
}

export default App;
