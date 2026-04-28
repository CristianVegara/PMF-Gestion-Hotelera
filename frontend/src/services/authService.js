const VALID_USER = "admin";
const VALID_PASS = "admin";

export const login = (username, password) => {
  if (username === VALID_USER && password === VALID_PASS) {
    const user = {
      username,
      role: "ADMIN"
    };

    localStorage.setItem("user", JSON.stringify(user));
    return true;
  }

  return false;
};

export const logout = () => {
  localStorage.removeItem("user");
};

export const getUser = () => {
  return JSON.parse(localStorage.getItem("user"));
};

export const isAuthenticated = () => {
  return localStorage.getItem("user") !== null;
};