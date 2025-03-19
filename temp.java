import { ConfigProvider, theme } from 'antd';
import { useState } from 'react';

function App() {
  const [darkMode, setDarkMode] = useState(false);

  return (
    <ConfigProvider theme={{ algorithm: darkMode ? theme.darkAlgorithm : theme.defaultAlgorithm }}>
      <button onClick={() => setDarkMode(!darkMode)}>Toggle Theme</button>
      {/* Your App Content */}
    </ConfigProvider>
  );
}

export default App;
