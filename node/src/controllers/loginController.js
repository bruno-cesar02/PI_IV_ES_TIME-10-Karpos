const { spawn } = require('child_process');
const path = require('path');

exports.login = (req, res) => {
  res.render('login', {
    css: 'login.css',
    title: 'Login',
    msg: req.session.msg || ''
  });
}

exports.loginForm = (req, res) => {
  const { email, password } = req.body;

    const processoJava = spawn(
        'java',
        [
            '-cp',
            'out/production/PI_IV_ES_TIME-10-Karpos',
            'cliente.Cliente',
            'login',
            email,
            password
        ],
        { cwd: path.resolve(__dirname, '..', '..', '..') }
    );


    let dadosRetornados = '"';

  processoJava.stdout.on('data', (data) => {
  dadosRetornados += data.toString();
  });

  processoJava.stderr.on('data', (data) => {
  console.error(`Erro do processo Java: ${data}`);
  });

  processoJava.on('close', (code) => {
  console.log(`Processo Java finalizado com código ${code}`);
  dadosRetornados = dadosRetornados.trim() + '"';
  console.log(dadosRetornados);
  dadosRetornados = JSON.parse(dadosRetornados);
  dadosRetornados = JSON.parse(dadosRetornados);
  console.log('Dados retornados do Java:', dadosRetornados);
  if (dadosRetornados.loginPermitido == 'true'){
    req.session.user = { 
    user: dadosRetornados.usuario.nomeCompleto,
    email: dadosRetornados.usuario.email,
    telefone: dadosRetornados.usuario.telefone,
    documento: dadosRetornados.usuario.documento,
    tamanhoHectares: dadosRetornados.usuario.tamanhoHectares
  };
    req.session.msg = '';
    res.redirect('/dashboard');
  } else {
    req.session.msg = dadosRetornados.msg;
    res.redirect('/login');
  }
  });

  /*
  if (dadosRetornados.loginPermitido){
    req.session.user = { 
    user: dados.usuario.nome,
    email: dados.usuario.email,
    telefone: dados.usuario.telefone,
    documento: dados.usuario.documento,
    nomeEmpresa: dados.usuario.nomeEmpresa,
    endereco: dados.usuario.endereco,
    tammanhoHectares: dados.usuario.tammanhoHectares,
    categoria: dados.usuario.categoria
  };
    req.session.msg = '';
    res.redirect('/dashboard');
  } else {
    req.session.msg = 'Usuário ou senha inválidos.';
    res.redirect('/login');
  }*/

};

exports.logout = (req, res) => {
  req.session.destroy((err) => {
    if (err) {
      console.error('Erro ao destruir a sessão:', err);
    }
    res.redirect('/login');
  });
};