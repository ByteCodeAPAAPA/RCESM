type SgiIn = {
    id: string;
    number: number;
    color: string;
    workcenter: string;
    event: string;
    actions: string;
    department: string;
    departmentName: string;
    employee: Employee;
    desiredDate: string;
    planDate: string;
    note: string;
    comment: string;
    agree: boolean;
    subSGI: SubSgiIn[];
    factExecution: FactExecutionSGIIn;
    parent?: string;
    documentId: string;
    imagesSGI: any;
}

type FactExecutionSGIIn = {
    id: string;
    executionDate: string;
    report: string;
    imagesFactSGI: any;
}

type SubSgiIn = {
    id: string;
    number: string;
    color: string;
    workcenter: string;
    event: string;
    actions: string;
    department: string;
    departmentName: string;
    employee: Employee;
    desiredDate: string;
    planDate: string;
    note: string;
    comment: string;
    agree: boolean;
    factExecution: FactExecutionSGIIn;
    parent?: string;
    documentId: string;
    imagesSGI: any;
}

