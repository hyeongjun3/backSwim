class SignUpError extends Error {
  constructor(...params) {
    super(...params);

    this.name = 'SignUpError';
  }
}

export { SignUpError };
