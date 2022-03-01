import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

export const withNavigate = (Component) => {
  const Wrapper = (props) => {
    const navigate = useNavigate();

    return <Component navigate={navigate} {...props} />;
  };

  return Wrapper;
};

export const withLocation = (Component) => {
  const Wrapper = (props) => {
    const { state } = useLocation();

    return <Component locationState={state} {...props} />;
  };

  return Wrapper;
};
