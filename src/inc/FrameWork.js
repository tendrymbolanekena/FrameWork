document.addEventListener("submit", function (event) {
    const form = event.target;
    const classe = form.dataset.object;

    if (!classe) {
        return;
    }

    let champ = form.querySelector('input[name="_fw_object"]');

    if (!champ) {
        champ = document.createElement("input");
        champ.type = "hidden";
        champ.name = "_fw_object";
        form.appendChild(champ);
    }

    champ.value = classe;
});