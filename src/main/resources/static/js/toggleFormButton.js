function enableFormElements() {
    let inputs = document.getElementsByTagName("input");
    setTimeout(function (){for (let i = 0; i < inputs.length; i++) {
        if (inputs[i].name !== "mentorId" && inputs[i].name !== "studentId" && inputs[i].name !== "coins") {
            inputs[i].disabled = !inputs[i].disabled;
        }
    }}, 50);
}

