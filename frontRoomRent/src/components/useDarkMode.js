import { useContext } from 'react';
import { DarkModeContext } from './DarkModeProvider';

const useDarkMode = () => {
  const context = useContext(DarkModeContext);

  if (!context) {
    console.warn(
      'useDarkMode debe usarse dentro de DarkModeProvider'
    );

    return {
      darkMode: false,
      toggleDarkMode: () => {},
    };
  }

  return context;
};

export default useDarkMode;
