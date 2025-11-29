// src/controllers/custosController.js

const { spawn } = require('child_process');
const path = require('path');

// GET /custos-registrados
// GET /custos-registrados
exports.mostrarCustosRegistrados = (req, res) => {
    let dataFiltro = req.query.data || '';

    if (dataFiltro.includes('-')) {
        const [ano, mes, dia] = dataFiltro.split('-');
        dataFiltro = `${dia}/${mes}/${ano}`;
    }

    let dadosRetornados = '';
    let processoJava;

    // decide qual comando Java rodar
    if (dataFiltro) {
        processoJava = spawn(
            'java',
            [
                '-cp',
                'out/production/PI_IV_ES_TIME-10-Karpos',
                'cliente.Cliente',
                'listacomfiltro',
                req.session.user.email,
                'field-costs',
                dataFiltro
            ],
            { cwd: path.resolve(__dirname, '..', '..', '..') }
        );
    } else {
        processoJava = spawn(
            'java',
            [
                '-cp',
                'out/production/PI_IV_ES_TIME-10-Karpos',
                'cliente.Cliente',
                'listasemfiltro',
                req.session.user.email,
                'field-costs'
            ],
            { cwd: path.resolve(__dirname, '..', '..', '..') }
        );
    }

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

        res.render('custos-registrados', {
            title: 'Custos Registrados',
            css: 'dashboard.css',
            cssExtra: 'caderno-campo.css',
            msg: req.session.msg || '',
            dados: req.session.user,
            dataFiltro: dataFiltro,
            custos: dadosRetornados.resultados || []
        });

        if (req.session) req.session.msg = '';
    });
};



// GET /novo-custo
exports.mostrarNovoCusto = (req, res) => {
  // Se vierem parâmetros na query (ex: redirecionamento de /novo-registro), repassá-los para a view
  const { dataAtividade, tipoAtividade, notas, valor } = req.query || {};

  res.render('novo-custo', {
    title: 'Novo Custo',
    css: 'dashboard.css',
    cssExtra: 'caderno-campo.css',
    dados: req.session.user,
    msg: req.session.msg || '',
    // parâmetros opcionais para pré-preenchimento
    dataAtividade: dataAtividade || '',
    tipoAtividade: tipoAtividade || '',
    notas: notas || '',
    valorPrefill: valor || ''

      // TODO: passar categorias, atividades associáveis, etc., se o backend fornecer.
    });
  };



  // POST /novo-custo - IMPLEMENTAÇÃO COMPLETA
  exports.salvarNovoCusto = (req, res) => {
      const {data, descricao, valor, categoria,} = req.body;

      // Pega email do usuário logado
      const usuarioEmail = req.session.user?.email || '';

      // Chama o Java via spawn
      const processoJava = spawn('java', [
          'cliente.Cliente',       // Classe principal
          'novoCusto',             // args[0] - ação
          usuarioEmail,            // args[1] - email do usuário
          data,                    // args[2] - data do custo
          descricao,               // args[3] - descrição
          valor,                   // args[4] - valor
          categoria,               // args[5] - categoria

      ], {
          cwd: path.resolve(__dirname, '..', '..', '..'),
          timeout: 8000
      });

      let dadosRetornados = '';

      processoJava.stdout.on('data', (data) => {
          dadosRetornados += data.toString();
      });

      processoJava.stderr.on('data', (data) => {
          console.error(`Erro do processo Java: ${data}`);
      });

      processoJava.on('error', (err) => {
          console.error('Erro ao spawnar Java:', err);
          if (req.session) req.session.msg = 'Erro ao conectar com o servidor';
          res.redirect('/novo-custo');
      });

      processoJava.on('close', (code) => {
          console.log(`Processo Java finalizado com código ${code}`);

          try {
              dadosRetornados = dadosRetornados.trim();

              // Parse igual ao login do seu colega
              let resposta = JSON.parse(dadosRetornados);
              if (typeof resposta === 'string') {
                  resposta = JSON.parse(resposta);
              }

              console.log('Resposta do Java:', resposta);

              if (resposta.sucesso) {
                  if (req.session) req.session.msg = 'Custo registrado com sucesso!';
                  res.redirect('/custos-registrados');
              } else {
                  if (req.session) req.session.msg = resposta.msg || 'Erro ao salvar custo';
                  res.redirect('/novo-custo');
              }
          } catch (err) {
              console.error('Erro ao parsear resposta:', err);
              console.error('Dados recebidos:', dadosRetornados);
              if (req.session) req.session.msg = 'Erro ao processar resposta do servidor';
              res.redirect('/novo-custo');
          }
      });
  };