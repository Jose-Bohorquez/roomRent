import { useNavigate } from "react-router-dom";

export default function DashCard({ title, desc, href, icon, internal }) {
  const navigate = useNavigate();

  const handleClick = () => {
    if (internal) {
      navigate(href);
    } else {
      window.open(href, "_blank", "noopener,noreferrer");
    }
  };

  return (
    <div
      role="button"
      tabIndex={0}
      onClick={handleClick}
      onKeyDown={e => (e.key === "Enter" || e.key === " ") && handleClick()}
      className="card card-hover p-6 flex flex-col gap-4 cursor-pointer group"
    >
      <span className="text-2xl w-11 h-11 flex items-center justify-center
                       rounded-lg bg-brand-100 dark:bg-brand-900/30
                       group-hover:bg-brand-200 dark:group-hover:bg-brand-800/40
                       transition-colors duration-150">
        {icon}
      </span>
      <div>
        <h3 className="text-sm font-bold text-stone-900 dark:text-white mb-1">{title}</h3>
        <p className="text-xs text-stone-500 dark:text-zinc-400 leading-relaxed">{desc}</p>
      </div>
    </div>
  );
}
