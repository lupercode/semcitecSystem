function drawChart(chartData) {

    var ctx = document.getElementById('myBarChart').getContext('2d');
    new Chart(ctx, {
      type: 'bar',
      data: {
        // labels: ['Informática Básica', 'Montegem e Manutenção', 'Python', 'Edição com CapCut'],
        labels: chartData.map(item => item.name),
        datasets: [{
          label: 'Nº de alunos por cursos',
          //data: [72, 36, 18, 18],
          data: chartData.map(item => item.quantity),
          borderWidth: 1
        }]
      },
      options: {
        indexAxis: 'y',
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
}
