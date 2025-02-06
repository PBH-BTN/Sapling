// 改进文字对比度
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.text-contrast').forEach(badge => {
        const bgColor = badge.dataset.bgColor;
        const rgb = hexToRgb(bgColor);
        const brightness = (rgb.r * 299 + rgb.g * 587 + rgb.b * 114) / 1000;
        badge.classList.add(brightness > 128 ? 'text-dark' : 'text-white');
    });

    function hexToRgb(hex) {
        hex = hex.replace('#', '');
        const bigint = parseInt(hex, 16);
        return {
            r: (bigint >> 16) & 255,
            g: (bigint >> 8) & 255,
            b: bigint & 255
        };
    }
});
