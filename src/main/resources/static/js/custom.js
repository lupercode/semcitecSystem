const toastElList = document.querySelectorAll('.toast')
const toastList = [...toastElList].map(toastEl => new bootstrap.Toast(toastEl, {}).show())

const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))

var receivers = {
    "Nome do Prefeito": "Prefeito",
    "Nome do Secretário": "Secretário"
};

var selectElement = document.querySelector("#dataListReceivers");

// Iterar sobre as chaves do objeto
for (var key in receivers) {
    if (receivers.hasOwnProperty(key)) {
        // Criar um elemento de opção
        var optionElement = document.createElement("option");
        // Definir o valor (value) e o texto (innerText) do elemento de opção
        optionElement.value = key;
        optionElement.innerText = key;
        // Adicionar o elemento de opção ao elemento select
        selectElement?.appendChild(optionElement);
    }
}

function changeSecondInput() {

    var receiver = document.querySelector("#receiver").value

    // Obter detalhes da fruta do objeto de dados
    var receiverTitle = receivers[receiver] || "";

    // Preencher o segundo input
    document.querySelector("#receiverPosition").value = receiverTitle;
}

var receiverElement = document.querySelector('#receiver');
receiverElement?.addEventListener("input", function(){
    changeSecondInput();
})

window.addEventListener('DOMContentLoaded', function() {
    if (receiverElement) changeSecondInput();

});

function formatter(mask, document){
	var i = document.value.length;
	var exit = mask.substring(0,1);
	var text = mask.substring(i);
	if (text.substring(0,1) != exit){document.value += text.substring(0,1);}
}

// Função para validar CPF
function validarCpf(cpf) {
    cpf = cpf.replace(/[^\d]+/g,''); // Remove caracteres não numéricos
    if(cpf === '' || cpf.length !== 11) {
        return false;
    }

    // Calcula os dígitos verificadores
    let soma = 0;
    for (let i = 0; i < 9; i++) {
        soma += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let resto = soma % 11;
    let digito1 = (resto < 2) ? 0 : 11 - resto;

    soma = 0;
    for (let i = 0; i < 10; i++) {
        soma += parseInt(cpf.charAt(i)) * (11 - i);
    }
    resto = soma % 11;
    let digito2 = (resto < 2) ? 0 : 11 - resto;

    // Verifica se os dígitos calculados são iguais aos dígitos informados
    return (parseInt(cpf.charAt(9)) === digito1) && (parseInt(cpf.charAt(10)) === digito2);
}

// Função para validar e exibir feedback visual
function validarCpfEExibirFeedback() {
    const cpfInput = document.querySelector('#inputCpf');
    const cpfError = document.querySelector('#cpfError');
    const cpfValue = cpfInput.value;

    if (validarCpf(cpfValue)) {
        // CPF válido
        cpfError.textContent = '';
        cpfInput.classList.remove('is-invalid');
        cpfInput.classList.add('is-valid');
    } else {
        // CPF inválido
        cpfError.textContent = 'CPF inválido';
        cpfInput.classList.remove('is-valid');
        cpfInput.classList.add('is-invalid');
    }
}

// Adiciona um ouvinte de evento para validar o CPF quando o campo é alterado
var cpfElement = document.querySelector('#inputCpf');
cpfElement?.addEventListener('input', validarCpfEExibirFeedback);

function classroomLoad() {
    var selectedCourse = document.getElementById('courseSelect').options[document.getElementById('courseSelect').selectedIndex].textContent;

    var classroomsByCourse = classrooms[selectedCourse]; //var classrooms está na página default

    var classroomsSelect = document.getElementById('classroomSelect');
    classroomsSelect.innerHTML = '';

    for (var i = 0; i < classroomsByCourse.length; i++) {
        var option = document.createElement('option');
        option.value = classroomsByCourse[i].uuid;
        option.text = classroomsByCourse[i].name;
        classroomsSelect.appendChild(option);
    }
}

