function toggleFormElements() {
    let inputs = document.getElementsByTagName("input");
    for (let i = 0; i < inputs.length; i++) {
        inputs[i].disabled = !inputs[i].disabled;
    }
}