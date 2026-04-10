import React from "react";

const StatCard = ({
  title,
  value,
  icon,
  color,
  sub,
  subIcon,
  subColor,
  iconBg,
  iconColor,
}) => {
  return (
    // Thêm h-full để các thẻ cao bằng nhau, items-start để icon luôn nằm ở góc trên nều thẻ cao ra
    <div
      className={`bg-surface-container-lowest p-6 rounded-xl border-l-4 ${color} shadow-sm flex items-start justify-between gap-4 h-full`}
    >
      {/* CỘT TRÁI: Chứa text. flex-1 và min-w-0 là "chìa khóa" giúp text tự động rớt dòng thay vì đẩy layout */}
      <div className="flex-1 min-w-0 space-y-1.5">
        {/* Tiêu đề: Tự động rớt xuống dòng nếu dài */}
        <p className="text-[10px] md:text-xs font-bold text-on-surface-variant uppercase tracking-widest leading-relaxed">
          {title}
        </p>

        {/* Con số: Dùng break-words để tự bẻ gãy chữ số nếu nó quá dài */}
        <div className="flex items-baseline flex-wrap gap-1">
          <h3 className="text-2xl md:text-3xl font-headline font-extrabold text-primary break-words">
            {value}
          </h3>

          {/* Chữ phụ cho trang Campaign */}
          {sub && !subIcon && (
            <span className="text-[10px] font-bold opacity-60 ml-1">{sub}</span>
          )}
        </div>

        {/* Dòng sub kèm Icon cho trang Lead: Rớt dòng an toàn */}
        {sub && subIcon && (
          <p
            className={`text-xs mt-1 flex items-start gap-1 ${subColor || "text-on-surface-variant"}`}
          >
            <span className="material-symbols-outlined text-[14px] shrink-0 mt-0.5">
              {subIcon}
            </span>
            <span className="break-words">{sub}</span>
          </p>
        )}
      </div>

      {/* CỘT PHẢI: Chứa Icon. shrink-0 là "chìa khóa" giúp Icon không bao giờ bị bóp méo hay văng ra ngoài */}
      <div className="shrink-0">
        {iconBg ? (
          <div
            className={`w-12 h-12 rounded-full flex items-center justify-center ${iconBg}`}
          >
            <span className={`material-symbols-outlined ${iconColor}`}>
              {icon}
            </span>
          </div>
        ) : (
          <span className="material-symbols-outlined opacity-20 text-3xl">
            {icon}
          </span>
        )}
      </div>
    </div>
  );
};

export default StatCard;
