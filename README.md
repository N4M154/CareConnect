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
  
/project-root
├── /api
│ ├── /config
│ │ └── db.js
│ ├── /controllers
│ │ ├── auth.controller.js
│ │ ├── course.controller.js
│ │ ├── user.controller.js
│ ├── /models
│ │ ├── course.model.js
│ │ ├── user.model.js
│ ├── /prisma
|    ├── schema.prisma
│ ├── /routes
│ │ ├── about.route.js
│ │ ├── auth.route.js
│ │ ├── compiler.route.js
│ │ ├── course.route.js
│ │ ├── user.route.js
│ ├── /utils
|    |── error.js
|    |── verifyUser.js 
│ ├── index.js
├── /client
│ ├── /public
│ │ └── vite.svg                                               
│ ├── /src
│ │ ├── /components
│ │ │ ├── Footer.jsx
│ │ │ ├── Header.jsx
│ │ │ ├── Layout.jsx
│ │ │ ├── OAuth.jsx
│ │ │ ├── PrivateRoute.jsx
│ │ │ ├── SecondaryHeader.jsx
│ │ │ ├── ShowHeader.jsx
│ │ ├── /images
│ │ ├── /pages
│ │ │ ├── About.jsx
│ │ │ ├── Array.jsx
│ │ │ ├── ArrayQuiz.jsx
│ │ │ └── C.jsx
│ │ │ └── CForeword.jsx
│ │ │ └── ConditionQuiz.jsx
│ │ │ └── Conditions.jsx
│ │ │ └── Cpp.jsx
│ │ │ └── CSharp.jsx
│ │ │ └── ForLoop.jsx
│ │ │ └── FunctionQuiz.jsx
│ │ │ └── Functions.jsx
│ │ │ └── HelloWorld.jsx
│ │ │ └── Home.jsx
│ │ │ └── Operators.jsx
│ │ │ └── OpQuiz.jsx
│ │ │ └── Profile.jsx
│ │ │ └── ProgressBar.jsx
│ │ │ └── SignIn.jsx
│ │ │ └── SignUp.jsx
│ │ │ └── tringQuiz.jsx
│ │ │ └── Strings.jsx
│ │ │ └── TheEnd.jsx
│ │ │ └── Topics.jsx
│ │ │ └── UploadAndCompile.jsx
│ │ │ └── VariableQuiz.jsx
│ │ │ └── While.jsx
│ │ │ └── WhileQuiz.jsx
│ │ │ └── arraycpp.jsx
│ │ │ └── h_worldcpp.jsx
│ │ │ └── operatorscpp.jsx
│ │ │ └── variables.jsx
│ │ │ └── variables_cpp.jsx
│ │ ├── /redux
│ │ │ ├── course
│ │ │    ├── courseSlice.js
│ │ │ ├── user
│ │ │    ├── userSlice.js
│ │ │ ├── store.js
│ │ ├── /utils
│ │ │ ├── pdf.js
│ │ ├── App.jx
│ │ ├── firebase.js
│ │ ├── index.css
│ │ ├── main.jsx
│ ├── index.html
│ ├── postcss.config.js
│ ├── tailwind.config.js
│ ├── vite.config.js
└── README.md



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


