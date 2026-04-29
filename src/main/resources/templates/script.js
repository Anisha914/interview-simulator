let questions = [];

// Load questions only if present
if(document.getElementById("questions")){
    fetch("http://localhost:8080/questions")
    .then(res => res.json())
    .then(data => {
        questions = data;
        let html = "";

       data.forEach(q => {
           html += `
               <div class="card question-card">
                   <p>${q.question}</p>
                   <textarea id="q${q.id}" class="input"></textarea>
               </div>
           `;
       });

        document.getElementById("questions").innerHTML = html;
    })
    .catch(err => console.error(err));
}

// Welcome text
if(document.getElementById("welcome")){
    document.getElementById("welcome").innerText =
        "Welcome " + localStorage.getItem("username");
}

// Submit
function submit(){
console.log("Username:", localStorage.getItem("username"));

    let answers = [];

    questions.forEach(q => {
        answers.push({
            qid: q.id,
            answer: document.getElementById("q"+q.id).value
        });
    });

    fetch("http://localhost:8080/submit", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            username: localStorage.getItem("username"),
            answers: answers
        })
    })
    .then(res => res.text())
    .then(data => {
        localStorage.setItem("result", data);
        window.location = "result.html";
    })
    .catch(err => console.error(err));
}

// Logout
function logout(){
    localStorage.clear();
    window.location = "index.html";
}

function toggleMenu(){
    document.getElementById("sidebar").classList.toggle("active");
}

function openLeaderboard(){
    window.location = "leaderboard.html";
}

console.log("Username:", localStorage.getItem("username"));

function loadLeaderboard() {
    console.log("Running...");

    fetch("http://localhost:8080/leaderboard")
    .then(res => res.json())
    .then(data => {
        console.log("Data:", data);

        let tbody = document.querySelector("#leaderboardTable tbody");

        tbody.innerHTML = "";

        data.forEach((r, index) => {
            tbody.innerHTML += `
                <tr>
                    <td>${index + 1}</td>
                    <td>${r.username}</td>
                    <td>${r.score}</td>
                </tr>
            `;
        });
    })
    .catch(err => console.error(err));
}

document.addEventListener("DOMContentLoaded", loadLeaderboard);