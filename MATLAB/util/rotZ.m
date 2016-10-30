function R = rotZ(gamma)

    R = diag([cos(gamma) cos(gamma) 1 1]);
    R(1,2) = -sin(gamma);
    R(2,1) = sin(gamma);

end