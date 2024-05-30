const express = require("express");
const bodyParser = require("body-parser");
const { pool } = require("./config/db.config.js");


const app = express();
const port = process.env.PORT;

const userRoute = require("./routes/UserRoute");
const courseRoute = require("./routes/CourseRoute");
const taskRoute = require("./routes/TaskRoute");

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use("/user", userRoute);
app.use("/course", courseRoute);
app.use("/task", taskRoute);

pool.connect(() => {
    console.log("Connected to database");
});

app.listen(port, () => {
    console.log(`Server is running on port ${port}`);
});
