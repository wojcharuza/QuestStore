function validateData() {
    let inputs = document.querySelectorAll(".levelExp");
    let errorDiv = document.getElementById("wrongInputsMessage");
    console.log(inputs.length);


    var nameErrors = [];
    for (let i = 1; i < inputs.length - 1; i++) {

        if(!inputs[i].value){
            nameErrors.push(`Level ${i+1} cannot be empty`)
        }

        if(Number(inputs[i].value) % 1 !== 0){
            nameErrors.push(`Level ${i+1} must be and integer`)
        }

        if (Number(inputs[i].value) <= 0){
            nameErrors.push(`Level ${i+1} cannot be lower than 0`);
        }
        if(Number(inputs[i].value) >= Number(inputs[i+1].value)){
            nameErrors.push(`Level ${i+1} cannot me higher or equal than level ${i+2}`)
        }
    }
    if(nameErrors.length === 0){
        errorDiv.innerHTML = "";
        enableFormElements();
        return true;

    }
    else{
        let errorMessages = "";
        for (let i = 0; i < nameErrors.length; i++){
            errorMessages += nameErrors[i] + "<br>"
        }
        errorDiv.innerHTML = errorMessages;
    }
    return false;

}