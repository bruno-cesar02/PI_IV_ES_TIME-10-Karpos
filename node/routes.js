const express = require('express');
const route = express.Router();
const indexController = require('./src/controllers/indexController');
const loginController = require('./src/controllers/loginController');
const registerController = require('./src/controllers/registerController');
const dadosController = require('./src/controllers/dadosController');
const dashboardController = require('./src/controllers/dashboardController');
const cadernoCampoController = require('./src/controllers/cadernoCampoController');
const custosController = require('./src/controllers/custosController');

const { redirecionarSeLogado } = require('./src/middlewares/redirecionarSeLogado');
const { verificarSeLogado } = require('./src/middlewares/verificarSeLogado');




//  Rota principal
route.get('/', redirecionarSeLogado, indexController.index);


//Rota de Registro
route.get('/register', redirecionarSeLogado,registerController.register);

route.post('/register',registerController.registerForm);


//  Rota de Login
route.get('/login', redirecionarSeLogado, loginController.login);

route.post('/login', loginController.loginForm);

// Rota de Logout

route.get('/logout', verificarSeLogado, loginController.logout);


// Rota de dados
route.get('/dados',verificarSeLogado, dadosController.dados);


// Rota do dashboard
route.get('/dashboard',verificarSeLogado, dashboardController.dashboard);

// ========= CADERNO DE CAMPO =========
route.get('/caderno-campo',verificarSeLogado,cadernoCampoController.mostrarCadernoCampo);

route.get('/novo-registro',verificarSeLogado,cadernoCampoController.mostrarNovoRegistro);

// ========= CUSTOS =========
route.get('/custos-registrados',verificarSeLogado,custosController.mostrarCustosRegistrados);

route.get('/novo-custo',verificarSeLogado,custosController.mostrarNovoCusto);



module.exports = route;