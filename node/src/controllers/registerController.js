const { spawn } = require('child_process');
const path = require('path');

exports.register = (req, res) => {
  res.render('register', {
    css: 'register.css',
    title: 'Register',
    msg: req.session.msg || ''
  });
}

exports.registerForm = (req, res) => {
  const { fullName, email, birthDate, phone, cpf, password, confirmPassword } = req.body;

    if (password !== confirmPassword) {
      req.session.msg = 'As senhas não coincidem.';
      console.log(req.session.msg);
      return res.redirect('/register');
    }
  
    const processoJava = spawn('java', ['cliente.Cliente', 'inserir', fullName, email, password, phone, cpf, birthDate ,"nenhum", 0 ,"nenhum"], {cwd: path.resolve(__dirname, '..', '..', '..')});
  
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
    dadosRetornados = JSON.parse(dadosRetornados);
    dadosRetornados = JSON.parse(dadosRetornados);
    console.log('Dados retornados do Java:', dadosRetornados);
    if (dadosRetornados.loginPermitido == 'true'){
      req.session.msg = 'cadastro realizado com sucesso';
      res.redirect('/login');
    } else {
      req.session.msg = dadosRetornados.msg;
      res.redirect('/register');
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