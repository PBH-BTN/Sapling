// 改进文字对比度
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.text-contrast').forEach(badge => {
        let bgColor = badge.dataset.bgColor;
        if (bgColor === undefined) {
            bgColor = badge.style.backgroundColor;
        }
        if (bgColor === undefined) {
            bgColor = badge.style.color;
        }
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

const tinymce_image_upload_handler = (blobInfo, progress) => new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.withCredentials = false;
    xhr.open('POST', '/bitbucket/upload');

    xhr.upload.onprogress = (e) => {
        progress(e.loaded / e.total * 100);
    };

    xhr.onload = () => {
        if (xhr.status === 403) {
            reject({message: 'HTTP Error: ' + xhr.status, remove: true});
            return;
        }

        if (xhr.status < 200 || xhr.status >= 300) {
            reject('HTTP Error: ' + xhr.status);
            return;
        }

        const json = JSON.parse(xhr.responseText);

        if (!json || typeof json.data.url != 'string') {
            if (json && json.message) {
                reject('API Error: ' + json.message);
                return;
            } else {
                reject('Invalid JSON: ' + xhr.status + ": " + xhr.responseText);
                return;
            }
        }

        resolve(json.data.url);
    };

    xhr.onerror = () => {
        reject('Image upload failed due to a XHR Transport error. Code: ' + xhr.status);
    };

    const formData = new FormData();
    formData.append('file', blobInfo.blob(), blobInfo.filename());

    xhr.send(formData);
});

document.addEventListener('DOMContentLoaded', function () {
    tinymce.init({
        selector: '.tinymce-editor',
        plugins: 'preview importcss searchreplace autolink autosave save code visualblocks visualchars fullscreen image link media codesample table charmap anchor insertdatetime advlist lists wordcount help charmap quickbars emoticons accordion',
        menubar: 'file edit view insert format tools table help',
        toolbar: "undo redo | accordion accordionremove | blocks fontfamily fontsize | bold italic underline strikethrough | align numlist bullist | link image | table media | lineheight outdent indent| forecolor backcolor removeformat | charmap emoticons | code fullscreen preview | save | anchor codesample",
        autosave_ask_before_unload: true,
        autosave_interval: '30s',
        autosave_prefix: '{path}{query}-{id}-',
        autosave_restore_when_empty: false,
        autosave_retention: '2m',
        image_advtab: true,
        height: 500,
        forced_root_block: false,
        image_caption: true,
        quickbars_selection_toolbar: 'bold italic | quicklink h2 h3 blockquote quickimage quicktable',
        noneditable_class: 'mceNonEditable',
        toolbar_mode: 'sliding',
        contextmenu: 'link image table',
        convert_urls: false,
        images_upload_handler: tinymce_image_upload_handler,
        // Markdown 配置
        extended_valid_elements: 'pre[class]',
        setup: function (editor) {
            editor.on('init', function () {
                this.getBody().style.fontSize = '14px';
            });
        }
    });
});