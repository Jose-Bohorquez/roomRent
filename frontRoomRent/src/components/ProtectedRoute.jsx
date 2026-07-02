import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// requiredRole puede ser string o array de strings (OR logic)
export default function ProtectedRoute({ children, requiredRole }) {
    const { user, loading } = useAuth();

    if (loading) return <div className="min-h-screen flex items-center justify-center">Cargando...</div>;
    if (!user) return <Navigate to="/login" replace />;

    if (requiredRole) {
        const roles = Array.isArray(requiredRole) ? requiredRole : [requiredRole];
        const hasRole = roles.some(r => user.authorities?.includes(r));
        if (!hasRole) return <Navigate to="/" replace />;
    }

    return children;
}
