exports.register = (req, res) => {
  res.render('register', {
    css: 'register.css',
    title: 'Register',
    msg: req.session.msg || ''
  });
}

exports.registerForm = (req, res) => {
  const { username, password } = req.body;

  // Simulação de verificação de usuário (substitua com lógica real)
  if (username === 'admin' && password === 'password') {
    req.session.user = { username };
    req.session.msg = '';
    res.redirect('/');
  } else {
    req.session.msg = 'Usuário ou senha inválidos.';
    res.redirect('/login');
  }
};