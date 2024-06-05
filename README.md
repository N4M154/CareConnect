# CareConnect

## What's This?
Hey there! Welcome to the CareConnect,Medicare Management System. This is a Java application where patients and doctors can manage their healthcare needs. Patients can book and cancel appointments, view prescriptions and bills, and even delete their accounts if they wish. Doctors can upload and view prescriptions, see their appointments, and also delete their accounts. It's designed to make healthcare management a breeze!

## Cool Features
- **Patients**:
  - Create an account and log in
  - Book and cancel appointments
  - View prescriptions and bills
  - Delete your account if needed

- **Doctors**:
  - Create an account and log in
  - Upload and view prescriptions
  - See your appointments
  - Delete your account if needed

## What You Need
- **Java Development Kit (JDK)** 8 or higher
- Oracle(connect with your username and password)
- JDBC driver for the database you plan to use
  



## Getting Started

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/your-repo-url.git
    ```

2. Install dependencies for the backend:
    ```bash
    cd api
    npm install
    ```

3. Install dependencies for the frontend:
    ```bash
    cd client
    npm install
    ```

### Running the Application

1. Start the backend server:(in the root)
    ```bash
    npm install @prisma/client
    npx prisma generate --schema=schema path
    npm run dev
    ```

2. Start the frontend development server:
    ```bash
    cd client
    npm run dev
    ```


## Project Structure

### Backend (`/api`)

- **/config**
  - `db.js`: Database configuration

- **/controllers**
  - `auth.controller.js`: Authentication logic
  - `course.controller.js`: Course handling logic
  - `user.controller.js`: User management logic

- **/models**
  - `course.model.js`: Course data schema
  - `user.model.js`: User data schema

- **/prisma**
  - `schema.prisma`: Prisma schema definition

- **/routes**
  - `about.route.js`: About page routes
  - `auth.route.js`: Authentication routes
  - `compiler.route.js`: Code compilation routes
  - `course.route.js`: Course routes
  - `user.route.js`: User routes

- **/utils**
  - `error.js`: Error handling utilities
  - `verifyUser.js`: User verification utilities

- `index.js`: Backend server entry point

### Frontend (`/client`)

- **/public**
  - `vite.svg`: Vite logo

- **/src**
  - **/components**
    - `Footer.jsx`: Footer component
    - `Header.jsx`: Header component
    - `Layout.jsx`: Layout component
    - `OAuth.jsx`: OAuth integration component
    - `PrivateRoute.jsx`: Private route component
    - `SecondaryHeader.jsx`: Secondary header component
    - `ShowHeader.jsx`: Header display component
  - **/images**: Image assets
  - **/pages**
    - `About.jsx`: About page
    - `Array.jsx`: Array lesson
    - `ArrayQuiz.jsx`: Array quiz
    - `C.jsx`: C programming language lesson
    - `CForeword.jsx`: C language foreword
    - `ConditionQuiz.jsx`: Conditions quiz
    - `Conditions.jsx`: Conditions lesson
    - `Cpp.jsx`: C++ lesson
    - `CSharp.jsx`: C# lesson
    - `ForLoop.jsx`: For loop lesson
    - `FunctionQuiz.jsx`: Functions quiz
    - `Functions.jsx`: Functions lesson
    - `HelloWorld.jsx`: Hello World lesson
    - `Home.jsx`: Home page
    - `Operators.jsx`: Operators lesson
    - `OpQuiz.jsx`: Operators quiz
    - `Profile.jsx`: User profile
    - `ProgressBar.jsx`: Progress bar component
    - `SignIn.jsx`: Sign in page
    - `SignUp.jsx`: Sign up page
    - `StringQuiz.jsx`: Strings quiz
    - `Strings.jsx`: Strings lesson
    - `TheEnd.jsx`: Conclusion page
    - `Topics.jsx`: Topics overview
    - `UploadAndCompile.jsx`: Upload and compile code
    - `VariableQuiz.jsx`: Variables quiz
    - `While.jsx`: While loop lesson
    - `WhileQuiz.jsx`: While loop quiz
    - `arraycpp.jsx`: C++ arrays lesson
    - `h_worldcpp.jsx`: C++ Hello World lesson
    - `operatorscpp.jsx`: C++ operators lesson
    - `variables.jsx`: Variables lesson
    - `variables_cpp.jsx`: C++ variables lesson
  - **/redux**
    - **course**
      - `courseSlice.js`: Course Redux slice
    - **user**
      - `userSlice.js`: User Redux slice
    - `store.js`: Redux store configuration
  - **/utils**
    - `pdf.js`: PDF utilities
  - `App.jsx`: Main React component
  - `firebase.js`: Firebase configuration
  - `index.css`: Global styles
  - `main.jsx`: Frontend entry point

- `index.html`: Main HTML file
- `postcss.config.js`: PostCSS configuration
- `tailwind.config.js`: Tailwind CSS configuration
- `vite.config.js`: Vite configuration
- 

