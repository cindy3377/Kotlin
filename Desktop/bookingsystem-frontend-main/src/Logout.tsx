import { Link } from "react-router-dom";
import "./Logout.css";
const Logout = () => {
    const handleLogin = () => {
        // Handle login logic here
      };
      
  return (
   
    <body id="body">
       
      <div>
      <h2>Login Page</h2>
      <br />
      <div className="login">
        <form id="login" onSubmit={handleLogin}>
          <label>
            <b>User Name</b>
          </label>
          <input type="text" name="Uname" id="Uname" placeholder="Username" />  
          <br />
          <br />
          <label>
            <b>Password</b>
          </label>
          <br />
          <input type="password" name="Pass" id="Pass" placeholder="Password" />
          <br />
          <br />
          <input type="submit" name="log" id="log" value="Log In Here" />
          <br />
          <br />
          
          <input type="checkbox" id="check" />
          <span id="rememberme"> Remember me </span>
          <br />
          <br />
           <a href="#" id="forget" > Forgot Password </a>
        </form>
        <br />
        <br />
        <Link to="/" id="home">Home</Link>
      </div>
      
    </div>
    
    
    </body>

  );
};

export default Logout;

