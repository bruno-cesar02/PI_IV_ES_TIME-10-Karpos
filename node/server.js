const express = require('express');
const app = express();
const routes = require('./routes');
const path = require('path');
const {middleware} = require('./src/middlewares/middleware');
const { spawn } = require('child_process');

/*
const processoJava = spawn('java', ['Retorno.java']);

let dadosRetornados = '';

processoJava.stdout.on('data', (data) => {
  dadosRetornados += data.toString();
});

processoJava.stderr.on('data', (data) => {
  console.error(`Erro do processo Java: ${data}`);
});

processoJava.on('close', (code) => {
  console.log(`Processo Java finalizado com código ${code}`);
  console.log('Dados retornados do Java:', dadosRetornados);
  // Aqui você pode iniciar o servidor Express após receber os dados do Java
});
*/


// Configuração da sessão

const session = require('express-session');
//const fsstore = require('session-file-store')(session);

/*fsstoreOptions = {
  path: path.resolve(__dirname, 'sessions'),
  ttl: 1000 * 60 * 60 * 24 * 7
};
*/

const sessionOptions = session({
  secret: 'senhasuperseguradomanokarpos',
  //store: new fsstore(fsstoreOptions),
  resave: false,
  saveUninitialized: false,
  cookie: {
    maxAge: 1000 * 60 * 60 * 24 * 7,
    httpOnly: true
  }
});
app.use(sessionOptions);

// ---- iniciar o servidor após iniciar a sessão no servidor Java para o mongodb

// ---- salvar a session no servidor Java para o mongodb

app.use(express.urlencoded({ extended: true }));

app.use(express.static(path.resolve(__dirname, 'src','public')));

app.set('views', path.resolve(__dirname, 'src', 'views'));
app.set('view engine', 'ejs');


// Nossos próprios middlewares
app.use(middleware);
app.use(routes);


// Ligando o servidor e sua porta
app.listen(3300, () => {
  console.log('Servidor rodando na porta:  127.0.0.1:3300');
});