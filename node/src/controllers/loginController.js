exports.login = (req, res) => {
  res.render('login', {
    css: 'login.css',
    title: 'Login',
    msg: req.session.msg || ''
  });
}

exports.loginForm = (req, res) => {
  const { username, password } = req.body;

  // enviar para o servidor do java os dados

  //receber true ou false se permitir ou nao o login junto com todos os dados do usuario
  
  //const dados = return do servidor java

  /*
  if (dados.loginPermitido){
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
  }

    */

  res.redirect('/dashboard');

};