import { EMAIL_REGEX, PASSWORD_REGEX } from "../constant/user-login-constant"


export const validateEmail = (email: string): boolean => {
  return EMAIL_REGEX.test(email);
}

export const validatePassword = (password: string): boolean => {
  return PASSWORD_REGEX.test(password);
}
