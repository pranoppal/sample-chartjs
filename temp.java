# Vite + React Project

## 🚀 Getting Started

This is the React-based UI for Neuron Healthcheck. Most UI components are sourced from **UWR widgets**. While some styling elements come from Ant Design, please avoid using it excessively—stick to UWR components whenever possible.. Most of the UI components are taken from UWR widgets. There are few style elements from ant design but please refrain from using those for every little thing use UWR components instead.

This project is built using **Vite** and **React** for a fast and optimized development experience.

### 📦 Prerequisites

Make sure you have the following installed:

- **Node.js** (v18 or later recommended)
- **npm** (comes with Node.js) or **yarn** / **pnpm**

### 🔧 Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/your-username/your-repo.git
   cd your-repo
   ```

2. Install dependencies:

   ```sh
   npm install  # or yarn install / pnpm install
   ```

### 🚀 Running the Development Server

Ensure the Spring Boot project is running; otherwise, API requests won't function. The Spring Boot project serves the React production build, so you can access the final UI at [**http://localhost:8080/status**](http://localhost:8080/status).. Note that the spring project uses the production build of the react project so you can access the final UI by going to [http://localhost:8080/status](http://localhost:8080/status).

Start the Vite development server:

```sh
npm run dev  # or yarn dev / pnpm dev
```

The app should be running at [**http://localhost:5173/**](http://localhost:5173/) (default Vite port).

### 📝 Contributing Guidelines

1. Before adding a new UI component (e.g., a radio button), check if it already exists in `commons`. If not, add it there first, then use it in your page. (e.g., a radio button), check if it's already present under `commons`. If not, add it there first and use it in your page., example radio button, please check if it's already present under `commons`. If not, then add one in `commons` and use that in your page.
2. Always prioritize using **UWR widgets** before introducing components from any other design library.
3. Keep API calls inside `loaders.ts` for simplicity and performance. If your use case is more complex, consider using **react-query**. to keep the application lightweight and efficient. For complex use cases, you may use **react-query**. as we are trying to keep this application as easy and light as possible. If your use case is complex, you can use **react-query** instead.

### 🏗️ Building for Production

You don't need to manually run `npm run build` command as the same will be done as a part of gradle build in the gitlab CI pipeline.

To create a production build:

```sh
npm run build  # or yarn build / pnpm build
```

The optimized build will be placed in `spring-boot/src/resources/static`, where it gets served by Spring Boot automatically.

### 🧪 Running Tests

If you have tests set up:

```sh
npm test  # or yarn test / pnpm test
```

### 🛠 Configuration

- Environment variables can be set in `.env` files (`.env.production`, `.env.development`, etc.).
- Modify `vite.config.js` for Vite-related configurations.

### 📂 Project Structure

```
📦 your-project
├── 📂 src            # Source code
│   ├── 📂 assets     # Static images
│   ├── 📂 commons    # Reusable components
│   ├── 📂 components # UI components categorised for each service 
│   ├── 📂 routes     # Handles the API call data from loader
│   ├── 📂 utils      # Contains APIs, colors, constants and other utility methods
│   │   ├── 📂 apis   # Contains API endpoints for each service
│   ├── 📜 Context.ts # Has env name, dark mode and refresh time context
│   ├── 📜 main.tsx   # Main entry point
│   ├── 📜 App.tsx    # Root React component
├── 📜 index.html     # Main HTML template
├── 📜 vite.config.js # Vite configuration
├── 📜 package.json   # Dependencies & scripts
└── 📜 README.md      # Project documentation
```

### 👥 Contributors

- **Your Name** - [your.email@example.com](mailto\:your.email@example.com)
- **Contributor 2** - [contributor2.email@example.com](mailto\:contributor2.email@example.com)

---

**Made with ❤️ using Vite and React.**

