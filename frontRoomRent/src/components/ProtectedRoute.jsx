import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ children, requiredRole }) {
    const { user, loading } = useAuth();

    if (loading) return <div className="min-h-screen flex items-center justify-center">Cargando...</div>;
    if (!user) return <Navigate to="/login" replace />;
    if (requiredRole && !user.authorities?.includes(requiredRole)) return <Navigate to="/" replace />;

    return children;
}
