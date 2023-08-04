import React from "react";

interface ButtonProps {
  name: string;
  age: number;
}

const Button = (props: ButtonProps) => {
  const { name, age } = props;
  return (
    <button>    
      I'm a button {name} {age}
    </button>
  );
};

export default Button;
