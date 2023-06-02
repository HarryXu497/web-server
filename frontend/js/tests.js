var interval = setInterval(async () => {
    await testHandler()
}, 500);

var testHandler = async () => {
    const url = window.location.href;

    const problemId = url.split("/")[3];

    const response = await fetch(`http://localhost:5000/problems/${problemId}/submissions`);

    const responseData = await response.json();

    if (Object.keys(responseData).length === 0) {
        clearInterval(interval);
    }

    console.log(responseData);
}