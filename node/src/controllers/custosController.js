// src/controllers/custosController.js

const { spawn } = require('child_process');
const path = require('path');

// GET /custos-registrados
exports.mostrarCustosRegistrados = (req, res) => {
    let dataFiltro = req.query.data || '';

    let msg = req.session.msg || '';

    req.session.msg = '';

    if (dataFiltro.includes('-')) {
        const [ano, mes, dia] = dataFiltro.split('-');
        dataFiltro = `${dia}/${mes}/${ano}`;
    }

    let dadosRetornados = '';
    let processoJava;

    // decide qual comando Java rodar
    if (dataFiltro) {
        processoJava = spawn('java',['cliente.Cliente','listacomfiltro',req.session.user.email,'field-costs',dataFiltro],{ cwd: path.resolve(__dirname, '..', '..', '..') });
    } else {
        processoJava = spawn('java',['cliente.Cliente','listasemfiltro',req.session.user.email,'field-costs'],{ cwd: path.resolve(__dirname, '..', '..', '..') });
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
            msg: msg || '',
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
  let { dataAtividade, tipoAtividade, notas, valor } = req.query || {};

  let msg = req.session.msg || '';

  req.session.msg = '';


  res.render('novo-custo', {
    title: 'Novo Custo',
    css: 'dashboard.css',
    cssExtra: 'caderno-campo.css',
    dados: req.session.user,
    msg: msg || '',
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
      let {data, descricao, valor, categoria, atividadeAssociada} = req.body;

      // Pega email do usuário logado
      const usuarioEmail = req.session.user?.email || '';

      if (data.includes('-')) {
        const [ano, mes, dia] = data.split('-');
        data = `${dia}/${mes}/${ano}`;
    }

      // Chama o Java via spawn
      const processoJava = spawn('java', [
          'cliente.Cliente',       
          'addatividadecusto',             
          usuarioEmail,            
          data,                    
          categoria,               
          descricao,                
          valor,
          atividadeAssociada         

      ], {
          cwd: path.resolve(__dirname, '..', '..', '..'),
          timeout: 8000
      });

      let dadosRetornados = '"';

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
              dadosRetornados = dadosRetornados.trim() + '"';

              // Parse igual ao login do seu colega
              let resposta = JSON.parse(dadosRetornados);
                  resposta = JSON.parse(resposta);

              console.log('Resposta do Java:', resposta);

              if (resposta.cadernoPermitido == 'true') {
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

  exports.deletarCusto = (req, res) => {

    let {data, tipo} = req.body;
    
      let dadosDelete = '';
    
      const processoJava = spawn('java', ['cliente.Cliente', 'deletar', req.session.user.email, data, tipo, "field-costs"], {cwd: path.resolve(__dirname, '..', '..', '..')});
      
      processoJava.stdout.on('data', (data) => {
        dadosDelete += data.toString();
      });
    
      processoJava.stderr.on('data', (data) => {
        console.error(`Erro do processo Java: ${data}`);
      });
    
      processoJava.on('close', (code) => {
        console.log(`Processo Java finalizado com código ${code}`);
        try {
          dadosDelete = dadosDelete.trim();
          console.log('Dados retornados do Java:', dadosDelete);
        } catch (e) {
          console.error('Erro ao parsear saída do Java:', e.message || e);
          dadosDelete = "";
        }
    
    
        if (dadosDelete == 'true'){
          req.session.msg = 'Atividade deletada com sucesso.';
          res.redirect('/custos-registrados');
        } else {
          req.session.msg = 'Erro ao deletar atividade.';  
          res.redirect('/custos-registrados');
        }
      });
  }