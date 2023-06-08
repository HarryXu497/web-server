const testsDiv = document.querySelector(".tests");
let testsDisplay;

function buildTests(responseData) {
    // Clear div
    while (testsDiv.lastChild) {
        testsDiv.removeChild(testsDiv.lastChild);
    }

    const testsContainer = document.createElement("div");
    testsContainer.classList.add("tests-display");

    const h1 = document.createElement("h1");
    const headerText = document.createTextNode("Tests");

    h1.classList.add("center")

    h1.append(headerText)

    testsDiv.append(h1);

    let completed = false;

    for (let i = 0; i < responseData.tests.length; i++) {
        const testDiv = document.createElement("div");

        let code = responseData.tests[i];

        if (completed) {
            code = "Done"
        }

        if ((code !== "AC") && (code !== "Pending")) {
            completed = true;
        }

        const content = document.createTextNode(`Test ${i + 1}: `);
        const span = document.createElement("span");

        const spanContent = document.createTextNode(`${code}`);

        span.append(spanContent);
        span.classList.add("status-code");

        switch (code) {
            case "AC":
                span.classList.add("status-code-green");
                break;

            case "WA":
                span.classList.add("status-code-red");
                break;

            case "TLE":
            case "OLE":
            case "MLE":
                span.classList.add("status-code-gray");
                break;

            case "RTE":
            case "IR":
                span.classList.add("status-code-yellow");
                break;
        }

        testDiv.appendChild(content);
        testDiv.appendChild(span)
        testDiv.classList.add("test-case")

        testsContainer.append(testDiv);
    }

    testsDiv.append(testsContainer);

    testsDisplay = testsContainer;
}

function updateTests(responseData) {
    if (!testsDisplay) {
        return;
    }

    let completed = false;

    const children = testsDisplay.children;
    let i = 0;

    for (const child of children) {
        let code = responseData.tests[i];

        if (completed) {
            code = "Done"
        }

        if ((code !== "AC") && (code !== "Pending")) {
            completed = true;
        }

        const span = child.lastElementChild;

        span.textContent = `${code}`;

        switch (code) {
            case "AC":
                span.classList.add("status-code-green");
                break;

            case "WA":
                span.classList.add("status-code-red");
                break;

            case "TLE":
            case "OLE":
            case "MLE":
                span.classList.add("status-code-gray");
                break;

            case "RTE":
            case "IR":
                span.classList.add("status-code-yellow");
                break;
        }

        i++;
    }
}

function compilationError(errorMessage) {
    // Clear div
    while (testsDiv.lastChild) {
        testsDiv.removeChild(testsDiv.lastChild);
    }

    const h1 = document.createElement("h1");
    const headerText = document.createTextNode("Compilation Error");

    h1.append(headerText)

    testsDiv.append(h1);

    const errorDiv = document.createElement("div");
    const text = document.createTextNode(errorMessage);
    errorDiv.append(text);
    errorDiv.classList.add("compilation-error");

    testsDiv.append(errorDiv);
}

function queued(responseData) {
    // Clear div
    while (testsDiv.lastChild) {
        testsDiv.removeChild(testsDiv.lastChild);
    }

    const h1 = document.createElement("h1");
    const headerText = document.createTextNode(`Submission Queued: Position ${responseData.position + 1}`);

    h1.classList.add("center")
    h1.append(headerText)

    testsDiv.append(h1);
}

function notFound() {
    // Clear div
    while (testsDiv.lastChild) {
        testsDiv.removeChild(testsDiv.lastChild);
    }

    const h1 = document.createElement("h1");
    const headerText = document.createTextNode("Not Found");

    h1.classList.add("center")
    h1.append(headerText)

    testsDiv.append(h1);
}

let build = true;

var interval = setInterval(async () => {
    await testHandler()
}, 250);

var testHandler = async () => {
    const responseData = await testsHandler();

    if ("error" in responseData) {
        compilationError(responseData.error);
        clearInterval(interval);
        return;
    }

    if (("tests" in responseData) && (build)) {
        buildTests(responseData);
        build = false;
    }

    if ("queued" in responseData) {
        queued(responseData);
    }

    if (responseData.completed) {
        if (Object.keys(responseData).length == 1) {
            notFound();
        }

        clearInterval(interval);
    }

    updateTests(responseData)
}

async function testsHandler() {
    const url = window.location.href;

    const problemId = url.split("/")[4];

    const response = await fetch(`http://localhost:5000/problems/${problemId}/submissions`);

    const responseData = await response.json();

    return responseData;
}

// testsHandler().then(res => buildTests(res))

