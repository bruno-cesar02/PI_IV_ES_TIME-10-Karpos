// src/controllers/cadernoCampoController.js

// GET /caderno-campo
// - Exibe a tela de histórico do Caderno de Campo (lista de atividades).
// - No futuro, deve buscar no backend Java/Mongo a lista de atividades
//   do usuário logado, possivelmente já aplicando filtros de data/tipo.
// - Também deverá tratar paginação ou ordenação, se o time decidir usar.
exports.mostrarCadernoCampo = (req, res) => {
  res.render('caderno-campo', {
    title: 'Meu Caderno de Campo',
    css: 'dashboard.css',
    cssExtra: 'caderno-campo.css',
    //active: 'historico', se optar por fazer as barras ficarem em negrito atraves do controller e nao do html
    // TODO: passar aqui a lista real de atividades vinda do backend
    atividades: [] // placeholder
  });
};


// GET /novo-registro
// - Exibe o formulário em branco para cadastro de uma nova atividade de campo.
// - No futuro, pode precisar carregar listas auxiliares do backend
//   (ex.: tipos de atividade, talhões, culturas) para preencher selects.
exports.mostrarNovoRegistro = (req, res) => {
  res.render('novo-registro', {
    title: 'Novo Registro',
    css: 'dashboard.css',
    cssExtra: 'caderno-campo.css',
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
  // TODO: implementar integração com backend para salvar nova atividade de campo.
  // Por enquanto, deixar vazio ou retornar um redirect fake se precisarem de demonstração.
};


// GET /caderno-campo/editar/:id
// - NÃO IMPLEMENTADO AINDA.
// - Deverá buscar uma atividade específica pelo ID no backend e
//   renderizar o formulário de edição preenchido com os dados atuais.
exports.editarRegistroForm = (req, res) => {
  // TODO: buscar atividade por ID no backend e renderizar uma tela de edição.
};


// POST /caderno-campo/editar/:id
// - NÃO IMPLEMENTADO AINDA.
// - Recebe os dados editados de uma atividade e envia para o backend atualizar.
// - Deve tratar validação, erros e redirecionar de volta para o histórico.
exports.atualizarRegistro = (req, res) => {
  // TODO: implementar atualização de atividade via backend.
};


// POST /caderno-campo/excluir
// - NÃO IMPLEMENTADO AINDA.
// - Vai receber um ou mais IDs de atividades marcadas na tabela
//   (checkboxes) e pedir ao backend para excluir esses registros.
exports.excluirRegistros = (req, res) => {
  // TODO: implementar exclusão em lote de atividades selecionadas.
};