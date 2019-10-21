function enableFormElements() {
    let inputs = document.getElementsByTagName("input");
    setTimeout(function (){for (let i = 0; i < inputs.length; i++) {
        if (inputs[i].name !== "mentorId" && inputs[i].name !== "studentId" && inputs[i].name !== "coins") {
            inputs[i].disabled = !inputs[i].disabled;
        }
        if (inputs[i].id === "saveButton" || inputs[i].id === "editButton") {
            inputs[i].hidden = !inputs[i].hidden;
        }
    }}, 50);
}


