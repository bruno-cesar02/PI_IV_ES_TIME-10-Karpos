const { spawn } = require('child_process');
const path = require('path');

// src/controllers/cadernoCampoController.js

// GET /caderno-campo
// - Exibe a tela de histórico do Caderno de Campo (lista de atividades).
// - No futuro, deve buscar no backend Java/Mongo a lista de atividades
//   do usuário logado, possivelmente já aplicando filtros de data/tipo.
// - Também deverá tratar paginação ou ordenação, se o time decidir usar.
exports.mostrarCadernoCampo = (req, res) => {

  let msg = req.session.msg || '';

  req.session.msg = '';

  let dataFiltro = req.query.data || '';

  if (dataFiltro.includes('-')) {
        const [ano, mes, dia] = dataFiltro.split('-');
        dataFiltro = `${dia}/${mes}/${ano}`;
    }


  let processoJava;

  if (dataFiltro) {
          processoJava = spawn('java',['cliente.Cliente','listacomfiltro',req.session.user.email,'field-metrics',dataFiltro],{ cwd: path.resolve(__dirname, '..', '..', '..') });
      } else {
          processoJava = spawn('java', ['cliente.Cliente', 'listasemfiltro', req.session.user.email, "field-metrics"], {cwd: path.resolve(__dirname, '..', '..', '..')});
      }


  let dadosRetornados = '';
  
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

      dadosRetornados = JSON.parse(dadosRetornados.trim());
      dadosRetornados = JSON.parse(dadosRetornados.trim());
      
      console.log('Dados retornados do Java:', dadosRetornados);
    } catch (e) {
      console.error('\n\nErro ao parsear saída do Java:', e.message || e);
      dadosRetornados = {};
    }

    res.render('caderno-campo', {
    title: 'Meu Caderno de Campo',
    css: 'dashboard.css',
    cssExtra: 'caderno-campo.css',
    msg: msg || '',
    dados: req.session.user,
    dataFiltro: dataFiltro,
    //active: 'historico', se optar por fazer as barras ficarem em negrito atraves do controller e nao do html
    // TODO: passar aqui a lista real de atividades vinda do backend
    atividades: dadosRetornados.resultados || [] // placeholder
  });
  });
};


// GET /novo-registro
// - Exibe o formulário em branco para cadastro de uma nova atividade de campo.
// - No futuro, pode precisar carregar listas auxiliares do backend
//   (ex.: tipos de atividade, talhões, culturas) para preencher selects.
exports.mostrarNovoRegistro = (req, res) => {
  let msg = req.session.msg || '';

  req.session.msg = '';

  res.render('novo-registro', {
    title: 'Novo Registro',
    css: 'dashboard.css',
    cssExtra: 'caderno-campo.css',
    msg: msg || '',
    dados: req.session.user
    //active: 'historico' se optar por fazer as barras ficarem em negrito atraves do controller e nao do html
    // TODO: passar dados auxiliares (tipos de atividade, etc.), se o backend fornecer
  });
};


// POST /novo-registro
// - NÃO IMPLEMENTADO AINDA.
// - Responsável por receber os dados do formulário de nova atividade.
// - Vai:
//     1. Ler os campos de req.body (data, tipo de atividade, notas, etc.).
//     2. Validar os dados (campos obrigatórios, formatos, etc.).
//     3. Enviar os dados para o servidor Java (ou API) responsável por gravar no MongoDB.
//     4. Tratar a resposta do backend (sucesso/erro).
//     5. Redirecionar o usuário de volta para:
//          - o histórico (/caderno-campo), com mensagem de sucesso, ou
//          - a mesma tela (/novo-registro) exibindo mensagens de erro.
// - IMPORTANTE: a forma de chamada (socket, REST, etc.) será decidida pelo time.
exports.salvarNovoRegistro = (req, res) => {
  let { dataAtividade, tipoAtividade, notas, valor } = req.body;

  if (!dataAtividade || !tipoAtividade) {
    req.session.msg = 'Por favor, preencha todos os campos obrigatórios.';
    return res.redirect('/novo-registro');
  }

  let dataatv = dataAtividade;

  if (dataatv.includes('/')) {
        const [dia,mes,ano] = dataatv.split('/');
        dataatv = `${mes}-${dia}-${ano}`;
  }

  if (dataAtividade.includes('-')) {
        const [ano, mes, dia] = dataAtividade.split('-');
        dataAtividade = `${dia}/${mes}/${ano}`;
  }

  // Converter valor para número para comparação segura
  const valorNum = parseFloat(String(valor).replace(',', '.')) || 0;

  const processoJava = spawn('java', ['cliente.Cliente', 'addatividade', req.session.user.email, dataAtividade, tipoAtividade, notas], {cwd: path.resolve(__dirname, '..', '..', '..')});

  let dadosRetornados = '"';
  
  processoJava.stdout.on('data', (data) => {
    dadosRetornados += data.toString();
  });

  processoJava.stderr.on('data', (data) => {
    console.error(`Erro do processo Java: ${data}`);
  });

  processoJava.on('close', (code) => {
    console.log(`Processo Java finalizado com código ${code}`);
    try {
      dadosRetornados = dadosRetornados.trim() + '"';
      dadosRetornados = JSON.parse(dadosRetornados);
      dadosRetornados = JSON.parse(dadosRetornados);
      console.log('Dados retornados do Java:', dadosRetornados);
    } catch (e) {
      console.error('Erro ao parsear saída do Java:', e.message || e);
      dadosRetornados = {};
    }

    // Se houver um valor de custo, redirecionar para /novo-custo com parâmetros para pré-preencher o formulário
    if (valorNum != 0) {
      req.session.msg = 'Atividade adicionada com sucesso. Agora, registre o custo associado a esta atividade.';
      const qs = new URLSearchParams({
        dataAtividade: dataatv,
        tipoAtividade: tipoAtividade,
        notas: notas || '',
        valor: String(valorNum)
      }).toString();
      return res.redirect(`/novo-custo?${qs}`);
    }

    if (dadosRetornados.cadernoPermitido == 'true'){
      req.session.msg = 'Atividade adicionada com sucesso.';
      res.redirect('/caderno-campo');
    } else {
      req.session.msg = dadosRetornados.msg || 'Erro ao adicionar atividade.';  
      res.redirect('/novo-registro');
    }
  });
};

exports.deletarRegistro = (req, res) => {

  let {data, tipo} = req.body;

  let dadosDelete;

  const processoJava = spawn('java', ['cliente.Cliente', 'deletar', req.session.user.email, data, tipo, "field-metrics"], {cwd: path.resolve(__dirname, '..', '..', '..')});
  
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
      res.redirect('/caderno-campo');
    } else {
      req.session.msg = 'Erro ao deletar atividade.';  
      res.redirect('/caderno-campo');
    }
  });
};

