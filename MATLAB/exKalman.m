init;
data = loadCSVAndPreprocess('../Data/20161002_120303_426.csv');


% We're trying to just estimate the angle from gyroscope


state = zeros(3,1); % angle
A = [1 0 0
    0 1 0
    0 0 1];

grav = zeros(size(data,1),3);
alpha = 0.99;
box = makeBox(0.5,1,4);
for i=2:size(data,1)
    dt = (data(i,1) - data(i-1,1))/1000;
    beta = alpha-dt;
    u = data(i, [GYROX, GYROY, GYROZ])';
    grav(i,:) = beta.*grav(i-1,:) + (1-beta).*data(i,[ACCX,ACCY,ACCZ]);
   grav(i,:) = grav(i,:)./norm(grav(i,:));
    
    
   % z = tan([ grav(i,3)/grav(i,2) ; grav(i,1)/grav(i,3); grav(i,2)/grav(i,1) ]);
    z = grav(i,:)';
   
    B = [ dt 0 0 
          0 dt 0
          0 0 dt];
     state = A*state + B*u; 
     
     if(mod(i,10)==0)
         figure(1)
         clf
         R1 = rotXYZ(state(1), state(2), state(3));
         R2 = rotXYZ( atan2(z(2),z(3)) , atan2(z(3),z(1)) ,atan2( z(2),z(1) ) );
         boxr1 = (R1*box')';
         boxr2 = (R2*box')';
         plot(0,0);
         hold on;
         plot3(boxr1(:,1), boxr1(:,2), boxr1(:,3), 'b-o');
         plot3(boxr2(:,1), boxr2(:,2), boxr2(:,3), 'r-o');
         hold off;
         grid on;
         axis equal;
         axis([-1,1,-1,1,-1,1]);
         view(3)
         pause(0.1)
         drawnow;
     end
end

