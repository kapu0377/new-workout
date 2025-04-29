let state = false;
async function checkId(mid){
    const result = await axios.get(`/check/${mid}`);
    return result.data;
}

document.addEventListener('DOMContentLoaded', function () {
    const accessPanel = document.getElementById('accesspanel');
    if (accessPanel) {
        accessPanel.addEventListener('submit', function(e) {
            e.preventDefault();
            state = !state;
            if (state) {
                document.getElementById("litheader").className = "poweron";
                document.getElementById("go").className = "";
                document.getElementById("go").value = "Initializing...";
            } else {
                document.getElementById("litheader").className = "";
                document.getElementById("go").className = "denied";
                document.getElementById("go").value = "Access Denied";
            }
        });
    }
    const firstVisibleInput = document.querySelector('input[type="text"]:not([type="hidden"]):not([disabled]):not([style*="display:none"]):not([style*="visibility:hidden"])');
    if (firstVisibleInput) {
        firstVisibleInput.focus();
    }
});