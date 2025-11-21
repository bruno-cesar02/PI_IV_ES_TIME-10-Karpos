// src/controllers/custosController.js

// GET /custos-registrados
// - Exibe a tela com a lista de custos já registrados.
// - No futuro, deve buscar no backend Java/Mongo os custos do usuário
//   (com possibilidade de aplicar filtros de data/categoria).
// - Pode também calcular resumos (total por categoria, etc.) se o time quiser.
exports.mostrarCustosRegistrados = (req, res) => {
  res.render('custos-registrados', {
    title: 'Custos Registrados',
    css: 'dashboard.css',
    cssExtra: 'caderno-campo.css',
    //active: 'historico', se optar por fazer as barras ficarem em negrito atraves do controller e nao do html
    // TODO: passar aqui a lista real de custos vinda do backend
    custos: [] // placeholder
  });
};


// GET /novo-custo
// - Exibe o formulário em branco para cadastro de um novo custo.
// - No futuro, pode precisar carregar listas auxiliares
//   (categorias de custo, atividades de campo para associação, etc.).
exports.mostrarNovoCusto = (req, res) => {
  res.render('novo-custo', {
    title: 'Novo Custo',
    css: 'dashboard.css',
    cssExtra: 'caderno-campo.css',
    //active: 'historico'se optar por fazer as barras ficarem em negrito atraves do controller e nao do html
    // TODO: passar categorias, atividades associáveis, etc., se o backend fornecer.
  });
};


// POST /novo-custo
// - NÃO IMPLEMENTADO AINDA.
// - Responsável por receber os dados do formulário de novo custo.
// - Vai:
//     1. Ler os campos de req.body (data, valor, descrição, categoria, atividade associada, etc.).
//     2. Validar os dados (valor numérico, data obrigatória, etc.).
//     3. Enviar para o backend Java/API salvar o custo no MongoDB.
//     4. Tratar resposta de sucesso/erro.
//     5. Redirecionar o usuário para:
//          - /custos-registrados (lista), ou
//          - a própria tela de /novo-custo com mensagens de erro.
exports.salvarNovoCusto = (req, res) => {
  // TODO: implementar integração com backend para salvar novo custo.
};


// GET /custos-registrados/editar/:id
// - NÃO IMPLEMENTADO AINDA.
// - Deve buscar um custo específico pelo ID e exibir o formulário preenchido
//   para edição.
exports.editarCustoForm = (req, res) => {
  // TODO: buscar custo por ID no backend e renderizar tela de edição.
};


// POST /custos-registrados/editar/:id
// - NÃO IMPLEMENTADO AINDA.
// - Recebe os dados editados de um custo e envia atualização para o backend.
exports.atualizarCusto = (req, res) => {
  // TODO: implementar atualização de custo via backend.
};


// POST /custos-registrados/excluir
// - NÃO IMPLEMENTADO AINDA.
// - Vai receber IDs dos custos selecionados na tabela
//   e solicitar ao backend a exclusão em lote.
exports.excluirCustos = (req, res) => {
  // TODO: implementar exclusão em lote de custos selecionados.
};
