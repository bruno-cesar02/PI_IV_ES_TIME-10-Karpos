const { spawn } = require('child_process');
const path = require('path');

exports.dashboard = (req, res) => {
  
      let msg = req.session.msg || '';
  
      req.session.msg = '';
  
      let dadosRetornados = '';
      let processoJava;
  
      processoJava = spawn('java',['cliente.Cliente','listasemfiltro',req.session.user.email,'field-costs'],{ cwd: path.resolve(__dirname, '..', '..', '..') });
  
      processoJava.stdout.on('data', (data) => {
          dadosRetornados = dadosRetornados.trim() + data.toString();
      });
  
      processoJava.stderr.on('data', (data) => {
          console.error(`Erro do processo Java: ${data}`);
      });
  
      processoJava.on('close', (code) => {
        console.log(`Processo Java finalizado com código ${code}`);

        try {
            dadosRetornados = '"' + dadosRetornados.trim() + '"';

            console.log(dadosRetornados);

            dadosRetornados = JSON.parse(dadosRetornados.trim());
            dadosRetornados = JSON.parse(dadosRetornados.trim());

            console.log('Dados retornados do Java:', dadosRetornados);
        } catch (e) {
            console.error('\n\nErro ao parsear saída do Java:', e.message || e);
            dadosRetornados = {};
        }

        let despesas = 0;
        let receitas = 0;

        if (dadosRetornados.resultados && Array.isArray(dadosRetornados.resultados)) {
            dadosRetornados.resultados.forEach(item => {
                const valor = parseFloat(item.custo);
                if (!isNaN(valor)) {
                    if (valor < 0) {
                        despesas += valor;
                    } else {
                        receitas += valor;
                    }
                }
            });
        }

        despesas = despesas.toFixed(2);
        receitas = receitas.toFixed(2);

        let porHectare = 0;
        if (parseFloat(req.session.user.tamanhoHectares) > 0) {

          if (parseFloat(req.session.user.tamanhoHectares) < 1){
            porHectare = (parseFloat(receitas) + parseFloat(despesas)).toFixed(2) * parseFloat(req.session.user.tamanhoHectares);
          } else {
            porHectare = ((parseFloat(receitas) + parseFloat(despesas)) / parseFloat(req.session.user.tamanhoHectares)).toFixed(2);
          }
        } else {
            porHectare = '0.00';
        }

        console.log(`Despesas: ${despesas}, Receitas: ${receitas}`);

    res.render('dashboard', {
    css: 'dashboard.css',
    title: 'Dashboard',
    dados: req.session.user,
    msg: msg || '',
    atividades: dadosRetornados.resultados[0] || {},
    despesas: despesas,
    receitas: receitas,
    porHectare: porHectare
  });
  });
}